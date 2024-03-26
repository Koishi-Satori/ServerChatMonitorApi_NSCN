# we only consider use azul zulu as its jre
FROM azul/zulu-openjdk:17-jre-headless-latest
MAINTAINER Satori_KKoishi <1580412105@qq.com>
LABEL version="1.0"
LABEL description="The backend server of the NS Server Char Monitor."
ADD top.kkoishi.ns.scmonitior-api.jar top.kkoishi.ns.scmonitior-api.jar
VOLUME ["/usr/local/KKoishi_/NS_ServerChatMonitor"]
# only consoder about linux deployment
# windows docker is just piece of shit
ENTRYPOINT ["/usr/lib/jvm/zulu17/bin/java", "-jar", "top.kkoishi.ns.scmonitior-api.jar"]
EXPOSE 5114