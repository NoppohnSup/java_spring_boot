FROM java:8-alpine

ENV profile local
ENV ms 128m
ENV mx 256m

EXPOSE 8080
ADD bom_spring_boot.jar app.jar
ADD ../version.txt version.txt
ADD security.policy security.policy

ADD logback.xml logback.xml
ADD logback-local.xml logback-local.xml

# log management
RUN mkdir -p /data/logs/fms/ \
  && touch /data/logs/fms/eventService.log \
  && ln -sf /dev/stdout /data/logs/fms/eventService.log \
  && ln -sf /dev/stderr /data/logs/fms/eventService.log

RUN echo 'Active profile => ' ${profile}

CMD java -jar \
          -Duser.timezone=Asia/Bangkok \
          -Dspring.profiles.active=${profile} \
          -Dfile.encoding=UTF-8 \
          -Dlogging.config=logback-${profile}.xml \
          -Xms${ms} \
          -Xmx${mx} app.jar ; \