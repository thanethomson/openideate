from setuptools import setup, find_packages

setup(
    name='openideate',
    version='0.01',
    description='Open source web-based ideation platform/idea repository.',
    author='openideate',
    author_email='dev@openideate.org',
    url='http://openideate.org/',
    packages=find_packages('src'),
    package_dir={'': 'src'},
    install_requires=['psycopg2',
                      'South',
                      'versiontools',
                      'django-debug-toolbar',
                      'django-ckeditor',
                      'django-follow',
                     ],
    include_package_data=True,
    license='Apache Software License',
    keywords='ideation idea repository',
    zip_safe=False,
    classifiers=[
        'Programming Language :: Python',
        'License :: OSI Approved :: Apache Software License',
        'Development Status :: 3 - Alpha',
        'Operating System :: OS Independent',
        'Framework :: Django',
        'Environment :: Web Environment',
        'Intended Audience :: Science/Research',
        'Topic :: Internet :: WWW/HTTP :: Dynamic Content',
    ],
)
