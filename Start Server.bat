cd .
set CLASSPATH=amd_server.jar
java -jar amd_server.jar

#Removido timezone por problema de horario no email
#java -jar -Duser.timezone=America/Sao_Paulo amd_server.jar