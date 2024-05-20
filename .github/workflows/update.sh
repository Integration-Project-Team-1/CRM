#!/bin/sh

# This script copies test.yml to production.yml and development.yml
# and makes scripted changes.


sed -e 's/test/development/' \
    -e 's/Test/Development/' \
    -e 's/TEST/DEV/' \
    -e 's/10\.2\.160\.9/10.2.160.10/' \
    -e 's/\*\*/dev/' test.yml > development.yml

sed -e 's/test/production/' \
    -e 's/Test/Production/' \
    -e 's/TEST/PROD/' \
    -e 's/10\.2\.160\.9/10.2.160.11/' \
    -e 's/\*\*/main/' test.yml > production.yml