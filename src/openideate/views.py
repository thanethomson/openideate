from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import Http404
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.db.models import Q
from django.template.loader import render_to_string

from openideate import constants
from openideate.models import UserAction, Idea
from follow.models import Follow



def home(request):
    """
    Renders the home page, which is basically just a stream listing
    with the default parameter set.
    """
    return stream_listing(request, constants.MENU_ITEM_TOP_DEFAULT, constants.MENU_ITEM_BOTTOM_DEFAULT)




@login_required
def stream_listing(request, who, what=constants.MENU_ITEM_BOTTOM_DEFAULT):
    """
    Renders the default template listing data according to the <who> and <what>
    parameters.
    
    TODO: Limit the number of stream items fetched.
    """
    # make sure the user's accessing a valid listing aspect
    if who not in dict(constants.MENU_ITEMS_TOP).keys():
        raise Http404
    if what not in dict(constants.MENU_ITEMS_BOTTOM).keys():
        raise Http404

    page_title = constants.PAGE_TITLES[who][what]
    stream = get_stream_items(request, who, what)
    # render each of the stream items to a string
    stream_strings = [render_to_string('openideate/stream/%s_%s.html' % (who, what), {'item': item}) for item in stream]

    return render_to_response('openideate/base.html', {
            'menu_items_top': constants.MENU_ITEMS_TOP,
            'menu_items_bottom': constants.MENU_ITEMS_BOTTOM,
            'menu_item_top_active': who,
            'menu_item_bottom_active': what,
            'page_title': page_title,
            'stream': stream_strings,
        },
        context_instance=RequestContext(request))



def get_stream_items(request, who, what):
    """
    Fetches the relevant list of stream items given the specified criteria.
    """
    if who == constants.MENU_ITEM_FOLLOWING:
        # get all the users that this user is following
        #following_users = [f.target_user for f in Follow.objects.filter(user=request.user, target_user__isnull=False)]
        # get all the ideas that this user is following
        #following_ideas = [f.target_idea for f in Follow.objects.filter(user=request.user, target_idea__isnull=False)]

        if what == constants.MENU_ITEM_ACTIVITY:
            return UserAction.objects.filter(Q(idea_version__idea__in=Idea.objects.filter(follow_idea__user=request.user)) |
                                             Q(user__follow_user__user=request.user)).order_by('-when')

        elif what == constants.MENU_ITEM_IDEAS:
            return Idea.objects.filter(follow_idea__user=request.user).order_by('-versions__created')
            
        #elif what == constants.MENU_ITEM_PROFILES:
            #return UserProfile.objects.filter(
            
    return []

