# constants relevant to the openideate app

from django.utils.translation import ugettext as _


MENU_ITEM_FOLLOWING = 'following'
MENU_ITEM_FOLLOWERS = 'followers'
MENU_ITEM_EVERYONE  = 'everyone'
MENU_ITEM_MYSELF    = 'myself'
MENU_ITEM_ACTIVITY  = 'activity'
MENU_ITEM_IDEAS     = 'ideas'
MENU_ITEM_PROFILES  = 'profiles'

MENU_ITEMS_TOP = (
    (MENU_ITEM_FOLLOWING, _("Who or What I'm Following")),
    (MENU_ITEM_FOLLOWERS, _("My Followers")),
    (MENU_ITEM_EVERYONE, _("Everyone")),
    (MENU_ITEM_MYSELF, _("Myself")),
)
# who or what will we display by default?
MENU_ITEM_TOP_DEFAULT = MENU_ITEM_FOLLOWING

MENU_ITEMS_BOTTOM = (
    (MENU_ITEM_ACTIVITY, _("Activity")),
    (MENU_ITEM_IDEAS, _("Ideas")),
    (MENU_ITEM_PROFILES, _("Profiles")),
)
# which aspect of what we're following will be displayed by default?
MENU_ITEM_BOTTOM_DEFAULT = MENU_ITEM_ACTIVITY


PAGE_TITLES = {
    MENU_ITEM_FOLLOWING: {
        MENU_ITEM_ACTIVITY: _("Activity for who or what you're following"),
        MENU_ITEM_IDEAS   : _("Ideas that you're following"),
        MENU_ITEM_PROFILES: _("People that you're following"),
    },
    MENU_ITEM_FOLLOWERS: {
        MENU_ITEM_ACTIVITY: _("Your followers' activity"),
        MENU_ITEM_IDEAS   : _("Your followers' ideas"),
        MENU_ITEM_PROFILES: _("Your followers"),
    },
    MENU_ITEM_EVERYONE: {
        MENU_ITEM_ACTIVITY: _("Everyone's activity"),
        MENU_ITEM_IDEAS   : _("Everyone's ideas"),
        MENU_ITEM_PROFILES: _("Everyone"),
    },
    MENU_ITEM_MYSELF: {
        MENU_ITEM_ACTIVITY: _("My activity"),
        MENU_ITEM_IDEAS   : _("My ideas"),
        MENU_ITEM_PROFILES: _("My profile"),
    },
}


