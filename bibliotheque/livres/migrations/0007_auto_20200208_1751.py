# Generated by Django 2.2.7 on 2020-02-08 16:51

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('livres', '0006_classement'),
    ]

    operations = [
        migrations.AlterField(
            model_name='livre',
            name='language',
            field=models.CharField(default='English', max_length=100),
        ),
    ]