from django.conf.urls.defaults import patterns, url
from openideate import views

urlpatterns = patterns('',
    url(r'^(?P<who>[a-z]+)/$', views.stream_listing, name='ideate-who'),
    url(r'^(?P<who>[a-z]+)/(?P<what>[a-z]+)/$', views.stream_listing, name='ideate-who-what'),
    url(r'^$', views.home, name='ideate-home'),
)
