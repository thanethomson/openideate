from django.contrib import admin
from openideate.models import Idea, IdeaVersion, IdeaTag, UserAction, \
                              UserPrivilege, UserProfile, UserCapability
                              


class IdeaVersionInline(admin.StackedInline):
    model = IdeaVersion
    extra = 0


class IdeaAdmin(admin.ModelAdmin):
    list_display = ['summary', 'last_updated', 'last_updated_by', 'tag_list']
    
    inlines = [
        IdeaVersionInline,
    ]
    
    def summary(self, obj):
        return obj.latest_version_summary
    
    def last_updated(self, obj):
        latest_version = obj.latest_version
        return latest_version.created if latest_version else None
    
    def last_updated_by(self, obj):
        latest_version = obj.latest_version
        return latest_version.created_by if latest_version else None
    
    def tag_list(self, obj):
        return ','.join(['%s' % tag for tag in obj.latest_version_tags])
        


class UserActionAdmin(admin.ModelAdmin):
    list_display = ['user', 'when', 'action', 'idea_version']
    
    
    
class UserProfileAdmin(admin.ModelAdmin):
    list_display = ['user', 'capability_list']
    
    def capability_list(self, obj):
        return ','.join(['%s' % capability for capability in obj.capabilities.all().order_by('name')])


admin.site.register(Idea, IdeaAdmin)
admin.site.register(UserAction, UserActionAdmin)
admin.site.register(UserProfile, UserProfileAdmin)