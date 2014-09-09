# Builds and runs the excel-validator application
#
# VERSION               0.0.1

FROM      ubuntu:14.04

# make sure the package repository is up to date
RUN echo "deb http://archive.ubuntu.com/ubuntu trusty main universe" > /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y --no-install-recommends git default-jre htop unzip wget

CMD myapp/activator stage

EXPOSE 9000