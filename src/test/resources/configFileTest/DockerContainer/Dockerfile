FROM ubuntu
MAINTAINER Kimbro Staken 

RUN apt-get install -y python-software-properties python python-setuptools ruby rubygems
RUN add-apt-repository ppa:chris-lea/node.js
RUN echo "deb http://us.archive.ubuntu.com/ubuntu/ precise universe" >> /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y nodejs 

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10
RUN echo "deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen" | tee -a /etc/apt/sources.list.d/10gen.list
RUN apt-get -y update
RUN apt-get -y install mongodb-10gen

RUN easy_install supervisor
RUN echo_supervisord_conf > /etc/supervisord.conf
RUN printf "[include]\nfiles = /var/www/Supervisorfile\n" >> /etc/supervisord.conf

ADD . /var/www

RUN cd /var/www ; npm install 

CMD ["/usr/local/bin/supervisord", "-n", "-c", "/etc/supervisord.conf"] [FROM ubuntu, MAINTAINER Kimbro Staken , ENV dutronflask dutronflask.mybluemix.net 
, RUN apt-get install -y python-software-properties python python-setuptools ruby rubygems, ENV dutronflask dutronflask.mybluemix.net 
, RUN add-apt-repository ppa:chris-lea/node.js, ENV dutronflask dutronflask.mybluemix.net 
, RUN echo "deb http://us.archive.ubuntu.com/ubuntu/ precise universe" >> /etc/apt/sources.list, ENV dutronflask dutronflask.mybluemix.net 
, RUN apt-get update, ENV dutronflask dutronflask.mybluemix.net 
, RUN apt-get install -y nodejs , ENV dutronflask dutronflask.mybluemix.net 
, RUN apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10, ENV dutronflask dutronflask.mybluemix.net 
, RUN echo "deb http://downloads-distro.mongodb.org/repo/ubuntu-upstart dist 10gen" | tee -a /etc/apt/sources.list.d/10gen.list, ENV dutronflask dutronflask.mybluemix.net 
, RUN apt-get -y update, ENV dutronflask dutronflask.mybluemix.net 
, RUN apt-get -y install mongodb-10gen, ENV dutronflask dutronflask.mybluemix.net 
, RUN easy_install supervisor, ENV dutronflask dutronflask.mybluemix.net 
, RUN echo_supervisord_conf > /etc/supervisord.conf, ENV dutronflask dutronflask.mybluemix.net 
, RUN printf "[include]\nfiles = /var/www/Supervisorfile\n" >> /etc/supervisord.conf, ENV dutronflask dutronflask.mybluemix.net 
, ADD . /var/www, ENV dutronflask dutronflask.mybluemix.net 
, RUN cd /var/www ; npm install , ENV dutronflask dutronflask.mybluemix.net 
, CMD ["/usr/local/bin/supervisord", "-n", "-c", "/etc/supervisord.conf"] ]