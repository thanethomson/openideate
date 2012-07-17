from django.db import models
from django.contrib.auth.models import User
from django.utils.translation import ugettext as _
from django.db.models.signals import pre_save
from django.dispatch import receiver

import follow.utils
from ckeditor.fields import RichTextField
from string import ascii_letters, digits
from openideate.exceptions import *
import datetime

from south.modelsinspector import add_introspection_rules
add_introspection_rules([], [r"^ckeditor\.fields\.RichTextField"])



class Idea(models.Model):
    """
    An abstraction of an idea, which is related to multiple idea versions.
    This is merely a way of tying various idea versions together.
    """

    def __unicode__(self):
        return u'%s' % self.latest_version_summary


    @property
    def sorted_versions(self):
        """
        Returns a QuerySet ordered by creation date from most recent to least.
        """
        return self.versions.order_by('-created')


    @property
    def latest_version(self):
        """
        Returns the most recent idea, or None if no versions exist for this idea.
        """
        return self.sorted_versions[0] if self.versions.count() > 0 else None


    @property
    def latest_version_summary(self):
        """
        Returns a summary of the latest version of this idea, if it exists.
        """
        return self.latest_version.summary if self.versions.count() > 0 else _("Empty idea")
        
        
    @property
    def latest_version_tags(self):
        """
        Returns a list of tags associated with this idea's latest version.
        """
        return self.latest_version.tags.all() if self.versions.count() > 0 else []


    def get_user_privilege(self, user):
        """
        Returns the privilege that the given user has with regard to this
        idea. If the user has no privileges regarding this idea, the return
        value will be None.
        """
        try:
            return self.user_privileges.get(user=user)
        except UserPrivilege.DoesNotExist:
            # by default, create a privilege object with no access
            return UserPrivilege.objects.create(user=user, idea=self,
                code=UserPrivilege.PRIVILEGE_NONE)


    def get_user_privilege_code(self, user):
        """
        Shortcut.
        """
        return self.get_user_privilege(user).code


    def grant_user_privilege(self, from_user, to_user, privilege_code):
        """
        Grants the privilege to the specified user (to_user) based on the
        privileges of from_user.
        """
        to_privilege = self.get_user_privilege(to_user)
        # if user 2 already has the privilege, skip this
        if privilege_code <= to_privilege.code:
            return

        from_privilege = self.get_user_privilege(from_user)

        # if user 1 wants to grant user 2 any privileges,
        # then user 1 needs admin privileges
        if from_privilege.code < UserPrivilege.PRIVILEGE_ADMIN:
            raise IdeaAdminPrivilegeRequired
        else:
            to_privilege.code = privilege_code
            to_privilege.save()


    def fork(self, user):
        """
        Forks the idea for the given user, making the given user the default
        administrator for the idea.
        """
        latest_version = self.latest_version
        # if for whatever reason this idea doesn't have any versions, we
        # can't fork it
        if not latest_version:
            raise Exception, _("Cannot fork empty idea.")

        forked_idea = Idea.objects.create()
        # grant admin privileges to the user
        UserPrivilege.objects.create(user=user, idea=forked_idea,
            code=UserPrivilege.PRIVILEGE_ADMIN)

        # make a copy of this idea's latest version
        forked_version = IdeaVersion.objects.create(idea=forked_idea,
            forked_from=latest_version,
            created_by=user,
            summary=latest_version.summary,
            content=latest_version.content,
        )

        return forked_idea



class IdeaVersion(models.Model):
    """
    An abstraction of a single version of an idea.
    """

    MODIFICATION_FLAG_SUMMARY = 's'
    MODIFICATION_FLAG_CONTENT = 'c'
    MODIFICATION_FLAG_TAGS    = 't'

    MODIFICATION_FLAGS = (
        (MODIFICATION_FLAG_SUMMARY, _('summary')),
        (MODIFICATION_FLAG_CONTENT, _('content')),
        (MODIFICATION_FLAG_TAGS,    _('tags')),
    )

    idea = models.ForeignKey(
        Idea,
        related_name='versions',
        help_text=_("The idea whose version this is."),
    )
    # NB: a new Idea must be created for every fork
    forked_from = models.ForeignKey(
        'self',
        blank=True,
        null=True,
        related_name="forks",
        help_text=_("The idea version from which this one was forked."),
    )
    summary = models.CharField(
        max_length=120,
        help_text=_("A short description of the idea (max 120 characters)."),
    )
    content = RichTextField(
        blank=True,
        null=True,
        config_name='ideate',
        help_text=_("The body of the idea."),
    )
    created = models.DateTimeField(
        auto_now_add=True,
        help_text=_("When this version of the idea was created."),
    )
    created_by = models.ForeignKey(
        User,
        related_name='idea_versions',
        help_text=_("Who created this version of the idea."),
    )
    tags = models.ManyToManyField(
        'IdeaTag',
        blank=True,
        null=True,
        related_name="idea_versions",
        help_text=_("The tags associated with this idea."),
    )

    modified_fields = models.CharField(
        blank=True,
        null=True,
        max_length=10, # for forward-compatibility
        help_text=_("A set of flags indicating which fields were modified "+
                    "from the previous version. See "+
                    "IdeaVersion.MODIFICATION_FLAGS. For example, 'sc' would "+
                    "mean both the summary and content fields were modified, "+
                    "and 's' would mean only the summary field was modified."),
    )


    def __unicode__(self):
        return u'%s (%s by %s)' % (self.summary,
            self.created.strftime("%Y-%m-%d %H:%M:%S"), self.created_by)


    def get_modified_fields(self):
        """
        Returns a readable list of which fields were modified since the
        previous version.
        """
        flags = dict(MODIFICATION_FLAGS)
        return [flags[c] for c in self.modification_flags]


    def get_sorted_tags(self):
        """
        Returns a list of tags associated with this idea, sorted
        alphabetically.
        """
        tags = [u'%s' % tag.name for tag in self.tags]
        tags.sort()
        return tags


    def save(self, *args, **kwargs):
        """
        Overrides the inherited save() function to automatically work out
        which fields were modified from the previous version.
        NB: It is assumed that an idea version will only ever be saved ONCE
        because no modifications to a particular version are allowed.

        In order to override the save check, pass in the
        override_save_check=True keyword argument.
        """
        override_save_check = kwargs.get('override_save_check', False)

        if self.id and not override_save_check:
            raise IdeaVersionAlreadySaved

        # first do the normal save operation
        super(IdeaVersion, self).save(*args, **kwargs)
        
        # now create the appropriate actions
        older_versions = self.idea.versions.filter(created__lt=self.created).order_by('-created')
        self.modified_fields = ''
        # if there is an older version
        if older_versions.count():
            prev_version = older_versions[0]
            if prev_version.summary != self.summary:
                self.modified_fields += MODIFICATION_FLAG_SUMMARY
                # add a new user action for this too
                # NB: we couldn't do this if we allowed for modification of
                #     ideas - we'd have to perform SELECTs to see if such
                #     actions already existed.
                self.add_action(UserAction.ACTION_MODIFY_IDEA_SUMMARY,
                    check_exists=override_save_check)
                
            if prev_version.content != self.content:
                self.modified_fields += MODIFICATION_FLAG_CONTENT
                # add new user action for this too
                self.add_action(UserAction.ACTION_MODIFY_IDEA_CONTENT,
                    check_exists=override_save_check)

            # if the tags have changed
            if prev_version.get_sorted_tags() != self.get_sorted_tags():
                self.modified_fields += MODIFICATION_FLAG_TAGS
                self.add_action(UserAction.ACTION_MODIFY_IDEA_TAGS,
                    check_exists=override_save_check)

        # check if the idea's been forked
        if self.forked_from:
            self.add_action(UserAction.ACTION_FORK_IDEA,
                check_exists=override_save_check)
        elif older_versions.count() == 0:
            # if the idea hasn't been forked, and there are no older versions,
            # it must mean the user's created a new idea
            self.add_action(UserAction.ACTION_NEW_IDEA,
                check_exists=override_save_check)
        else:
            # if the idea hasn't been forked, and it's not a new idea, then
            # it must just be a new version
            # but we can't allow a new version without any modifications from
            # the previous one...
            if not self.modified_fields:
                raise IdeaVersionNotChanged

            self.add_action(UserAction.ACTION_NEW_IDEA_VERSION,
                check_exists=override_save_check)


    def add_action(self, action, check_exists=False):
        """
        Helper shortcut to add user actions.
        """
        if check_exists:
            if UserAction.objects.filter(user=self.created_by,
                    action=action,
                    idea_version=self).count() > 0:
                # skip adding the action if it already exists
                return

        # create the action
        UserAction.objects.create(user=self.created_by,
            action=action,
            idea_version=self,
        )



class IdeaTag(models.Model):
    """
    Represents a single tag associated with an idea. The default tags that
    will be created will be Academic, Commercial, Blue Sky and Miscellaneous.
    """

    name = models.CharField(
        max_length=50,
        unique=True,
        help_text=_("This tag's name."),
    )

    def __unicode__(self):
        return u'%s' % self.name


    def save(self, *args, **kwargs):
        """
        First converts the name to lowercase before saving it and strips
        any unwelcome characters.

        TODO: Think about internationalisation support here. This will only
        support standard English alphabets and languages that use the English
        alphabets.
        """
        self.name = ''.join([c for c in self.name.lower() if c in ascii_letters+digits+' '])
        super(IdeaTag, self).save(*args, **kwargs)



class UserAction(models.Model):
    """
    A representation of a single user action that, for whatever reason, we want
    to remember.
    """

    ACTION_NEW_IDEA            = 0
    ACTION_NEW_IDEA_VERSION    = 1
    ACTION_FORK_IDEA           = 2
    ACTION_MODIFY_IDEA_SUMMARY = 3
    ACTION_MODIFY_IDEA_CONTENT = 4
    ACTION_MODIFY_IDEA_TAGS    = 5

    ACTIONS = (
        (ACTION_NEW_IDEA,            _("added an idea")),
        (ACTION_NEW_IDEA_VERSION,    _("added an idea version")),
        (ACTION_FORK_IDEA,           _("forked an idea")),
        (ACTION_MODIFY_IDEA_SUMMARY, _("modified an idea's summary")),
        (ACTION_MODIFY_IDEA_CONTENT, _("modified an idea's content")),
        (ACTION_MODIFY_IDEA_TAGS,    _("modified an idea's tags")),
    )


    user = models.ForeignKey(
        User,
        related_name='actions',
        help_text=_("The user that performed this action."),
    )
    when = models.DateTimeField(
        auto_now_add=True,
        help_text=_("When this action occurred."),
    )
    action = models.IntegerField(
        choices=ACTIONS,
        help_text=_("The action that was performed."),
    )
    idea_version = models.ForeignKey(
        IdeaVersion,
        blank=True,
        null=True,
        related_name='actions',
        help_text=_("The relevant idea version for this action, if any."),
    )

    def __unicode__(self):
        return self.get_action_display() % {'name': self.user,
            'summary': self.idea_version.summary if self.idea_version else \
                _("(no summary for idea version)")}



class UserPrivilege(models.Model):
    """
    Instance-level privileges between users and ideas.
    """

    # the greater values are higher privileges
    PRIVILEGE_NONE  = 0 # the user has no privileges
    PRIVILEGE_READ  = 1 # the user can view the idea and its history
    PRIVILEGE_WRITE = 2 # the user can modify the idea
    PRIVILEGE_ADMIN = 3 # the user has administrator rights to grant others privileges and to delete the idea

    PRIVILEGES = (
        (PRIVILEGE_READ,  _('read')),
        (PRIVILEGE_WRITE, _('write')),
        (PRIVILEGE_ADMIN, _('administrator')),
    )

    user = models.ForeignKey(
        User,
        related_name='idea_privileges',
        help_text=_("The user relevant to this privilege."),
    )
    idea = models.ForeignKey(
        Idea,
        related_name='user_privileges',
        help_text=_("The idea to which this privilege applies."),
    )
    code = models.SmallIntegerField(
        choices=PRIVILEGES,
        help_text=_("The privilege code connecting this user to this idea."),
    )

    def __unicode__(self):
        return u'%s has %s to %s' % (self.user, self.get_code_display(),
            self.idea)



class UserProfile(models.Model):
    """
    A representation of a user profile, attached to each user object.

    TODO: Add avatar (Gravatar support?), bio, capabilities.
    """

    user = models.OneToOneField(
        User,
        help_text=_("The user to whom this profile belongs."),
        related_name='profile',
    )
    capabilities = models.ManyToManyField(
        'UserCapability',
        blank=True,
        null=True,
        related_name='profiles', # the profiles of users matching the particular capability
        help_text=_("This user's capabilities."),
    )

    def __unicode__(self):
        return u'%s' % self.user
    
    @property
    def name(self):
        return (u'%s %s' % (self.user.first_name, self.user.last_name)) if self.user.first_name and self.user.last_name else \
            self.user.first_name if self.user.first_name else \
            self.user.username


@receiver(pre_save, sender=User)
def user_pre_save(sender, **kwargs):
    """
    Function to make sure each user has a user profile associated
    with them.
    """
    if 'instance' in kwargs:
        try:
            profile = kwargs['instance'].profile
        except UserProfile.DoesNotExist:
            # if it doesn't exist, create it
            kwargs['instance'].profile = UserProfile.objects.create(
                user=kwargs['instance'],
            )
    


class UserCapability(models.Model):
    """
    Some sort of capability that the user has. For example, this could be
    "C++ programming", or "philosophising", or "rocket engineering". This is
    a way of "tagging" users, basically, to allow for matching of ideas to
    users so that the matching users can implement these ideas.
    """

    name = models.CharField(
        max_length=100,
        unique=True,
        help_text=_("This capability's name. Note that this must be unique for searching purposes."),
    )

    def __unicode__(self):
        return u'%s' % self.name

    def save(self, *args, **kwargs):
        """
        Overrides the save method to first convert the capability name to lowercase.
        """
        self.name = self.name.lower()
        super(UserCapability, self).save(*args, **kwargs)



# things that users can follow...
# each other:
follow.utils.register(User)
# ideas:
follow.utils.register(Idea)

