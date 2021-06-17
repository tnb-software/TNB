FROM kennethreitz/httpbin:latest

ADD server.cert server.key run.sh /httpbin/
RUN chmod -R a+rw /httpbin/

EXPOSE 80
EXPOSE 443

CMD ["bash", "/httpbin/run.sh"]
