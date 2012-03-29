# encoding: utf-8
import datetime
from south.db import db
from south.v2 import SchemaMigration
from django.db import models

class Migration(SchemaMigration):

    def forwards(self, orm):
        
        # Adding model 'Idea'
        db.create_table('openideate_idea', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
        ))
        db.send_create_signal('openideate', ['Idea'])

        # Adding model 'IdeaVersion'
        db.create_table('openideate_ideaversion', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('idea', self.gf('django.db.models.fields.related.ForeignKey')(related_name='versions', to=orm['openideate.Idea'])),
            ('forked_from', self.gf('django.db.models.fields.related.ForeignKey')(blank=True, related_name='forks', null=True, to=orm['openideate.IdeaVersion'])),
            ('summary', self.gf('django.db.models.fields.CharField')(max_length=120)),
            ('content', self.gf('ckeditor.fields.RichTextField')(null=True, blank=True)),
            ('created', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('created_by', self.gf('django.db.models.fields.related.ForeignKey')(related_name='idea_versions', to=orm['auth.User'])),
            ('modified_fields', self.gf('django.db.models.fields.CharField')(max_length=10)),
        ))
        db.send_create_signal('openideate', ['IdeaVersion'])

        # Adding M2M table for field tags on 'IdeaVersion'
        db.create_table('openideate_ideaversion_tags', (
            ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True)),
            ('ideaversion', models.ForeignKey(orm['openideate.ideaversion'], null=False)),
            ('ideatag', models.ForeignKey(orm['openideate.ideatag'], null=False))
        ))
        db.create_unique('openideate_ideaversion_tags', ['ideaversion_id', 'ideatag_id'])

        # Adding model 'IdeaTag'
        db.create_table('openideate_ideatag', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(unique=True, max_length=50)),
        ))
        db.send_create_signal('openideate', ['IdeaTag'])

        # Adding model 'UserAction'
        db.create_table('openideate_useraction', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(related_name='actions', to=orm['auth.User'])),
            ('when', self.gf('django.db.models.fields.DateTimeField')(auto_now_add=True, blank=True)),
            ('action', self.gf('django.db.models.fields.IntegerField')()),
            ('idea_version', self.gf('django.db.models.fields.related.ForeignKey')(blank=True, related_name='actions', null=True, to=orm['openideate.IdeaVersion'])),
        ))
        db.send_create_signal('openideate', ['UserAction'])

        # Adding model 'UserPrivilege'
        db.create_table('openideate_userprivilege', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.ForeignKey')(related_name='idea_privileges', to=orm['auth.User'])),
            ('idea', self.gf('django.db.models.fields.related.ForeignKey')(related_name='user_privileges', to=orm['openideate.Idea'])),
            ('code', self.gf('django.db.models.fields.SmallIntegerField')()),
        ))
        db.send_create_signal('openideate', ['UserPrivilege'])

        # Adding model 'UserProfile'
        db.create_table('openideate_userprofile', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('user', self.gf('django.db.models.fields.related.OneToOneField')(to=orm['auth.User'], unique=True)),
        ))
        db.send_create_signal('openideate', ['UserProfile'])

        # Adding M2M table for field capabilities on 'UserProfile'
        db.create_table('openideate_userprofile_capabilities', (
            ('id', models.AutoField(verbose_name='ID', primary_key=True, auto_created=True)),
            ('userprofile', models.ForeignKey(orm['openideate.userprofile'], null=False)),
            ('usercapability', models.ForeignKey(orm['openideate.usercapability'], null=False))
        ))
        db.create_unique('openideate_userprofile_capabilities', ['userprofile_id', 'usercapability_id'])

        # Adding model 'UserCapability'
        db.create_table('openideate_usercapability', (
            ('id', self.gf('django.db.models.fields.AutoField')(primary_key=True)),
            ('name', self.gf('django.db.models.fields.CharField')(unique=True, max_length=100)),
        ))
        db.send_create_signal('openideate', ['UserCapability'])


    def backwards(self, orm):
        
        # Deleting model 'Idea'
        db.delete_table('openideate_idea')

        # Deleting model 'IdeaVersion'
        db.delete_table('openideate_ideaversion')

        # Removing M2M table for field tags on 'IdeaVersion'
        db.delete_table('openideate_ideaversion_tags')

        # Deleting model 'IdeaTag'
        db.delete_table('openideate_ideatag')

        # Deleting model 'UserAction'
        db.delete_table('openideate_useraction')

        # Deleting model 'UserPrivilege'
        db.delete_table('openideate_userprivilege')

        # Deleting model 'UserProfile'
        db.delete_table('openideate_userprofile')

        # Removing M2M table for field capabilities on 'UserProfile'
        db.delete_table('openideate_userprofile_capabilities')

        # Deleting model 'UserCapability'
        db.delete_table('openideate_usercapability')


    models = {
        'auth.group': {
            'Meta': {'object_name': 'Group'},
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '80'}),
            'permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': "orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'})
        },
        'auth.permission': {
            'Meta': {'ordering': "('content_type__app_label', 'content_type__model', 'codename')", 'unique_together': "(('content_type', 'codename'),)", 'object_name': 'Permission'},
            'codename': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'content_type': ('django.db.models.fields.related.ForeignKey', [], {'to': "orm['contenttypes.ContentType']"}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '50'})
        },
        'auth.user': {
            'Meta': {'object_name': 'User'},
            'date_joined': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'email': ('django.db.models.fields.EmailField', [], {'max_length': '75', 'blank': 'True'}),
            'first_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'groups': ('django.db.models.fields.related.ManyToManyField', [], {'to': "orm['auth.Group']", 'symmetrical': 'False', 'blank': 'True'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'is_active': ('django.db.models.fields.BooleanField', [], {'default': 'True'}),
            'is_staff': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'is_superuser': ('django.db.models.fields.BooleanField', [], {'default': 'False'}),
            'last_login': ('django.db.models.fields.DateTimeField', [], {'default': 'datetime.datetime.now'}),
            'last_name': ('django.db.models.fields.CharField', [], {'max_length': '30', 'blank': 'True'}),
            'password': ('django.db.models.fields.CharField', [], {'max_length': '128'}),
            'user_permissions': ('django.db.models.fields.related.ManyToManyField', [], {'to': "orm['auth.Permission']", 'symmetrical': 'False', 'blank': 'True'}),
            'username': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '30'})
        },
        'contenttypes.contenttype': {
            'Meta': {'ordering': "('name',)", 'unique_together': "(('app_label', 'model'),)", 'object_name': 'ContentType', 'db_table': "'django_content_type'"},
            'app_label': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'model': ('django.db.models.fields.CharField', [], {'max_length': '100'}),
            'name': ('django.db.models.fields.CharField', [], {'max_length': '100'})
        },
        'openideate.idea': {
            'Meta': {'object_name': 'Idea'},
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'})
        },
        'openideate.ideatag': {
            'Meta': {'object_name': 'IdeaTag'},
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '50'})
        },
        'openideate.ideaversion': {
            'Meta': {'object_name': 'IdeaVersion'},
            'content': ('ckeditor.fields.RichTextField', [], {'null': 'True', 'blank': 'True'}),
            'created': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'}),
            'created_by': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'idea_versions'", 'to': "orm['auth.User']"}),
            'forked_from': ('django.db.models.fields.related.ForeignKey', [], {'blank': 'True', 'related_name': "'forks'", 'null': 'True', 'to': "orm['openideate.IdeaVersion']"}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'idea': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'versions'", 'to': "orm['openideate.Idea']"}),
            'modified_fields': ('django.db.models.fields.CharField', [], {'max_length': '10'}),
            'summary': ('django.db.models.fields.CharField', [], {'max_length': '120'}),
            'tags': ('django.db.models.fields.related.ManyToManyField', [], {'related_name': "'idea_versions'", 'symmetrical': 'False', 'to': "orm['openideate.IdeaTag']"})
        },
        'openideate.useraction': {
            'Meta': {'object_name': 'UserAction'},
            'action': ('django.db.models.fields.IntegerField', [], {}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'idea_version': ('django.db.models.fields.related.ForeignKey', [], {'blank': 'True', 'related_name': "'actions'", 'null': 'True', 'to': "orm['openideate.IdeaVersion']"}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'actions'", 'to': "orm['auth.User']"}),
            'when': ('django.db.models.fields.DateTimeField', [], {'auto_now_add': 'True', 'blank': 'True'})
        },
        'openideate.usercapability': {
            'Meta': {'object_name': 'UserCapability'},
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'name': ('django.db.models.fields.CharField', [], {'unique': 'True', 'max_length': '100'})
        },
        'openideate.userprivilege': {
            'Meta': {'object_name': 'UserPrivilege'},
            'code': ('django.db.models.fields.SmallIntegerField', [], {}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'idea': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'user_privileges'", 'to': "orm['openideate.Idea']"}),
            'user': ('django.db.models.fields.related.ForeignKey', [], {'related_name': "'idea_privileges'", 'to': "orm['auth.User']"})
        },
        'openideate.userprofile': {
            'Meta': {'object_name': 'UserProfile'},
            'capabilities': ('django.db.models.fields.related.ManyToManyField', [], {'related_name': "'profiles'", 'symmetrical': 'False', 'to': "orm['openideate.UserCapability']"}),
            'id': ('django.db.models.fields.AutoField', [], {'primary_key': 'True'}),
            'user': ('django.db.models.fields.related.OneToOneField', [], {'to': "orm['auth.User']", 'unique': 'True'})
        }
    }

    complete_apps = ['openideate']
