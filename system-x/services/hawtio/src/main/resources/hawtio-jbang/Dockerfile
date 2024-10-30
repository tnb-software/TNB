FROM registry.access.redhat.com/ubi9/openjdk-21:1.20

ARG HAWTIO_REPOS_TAG=hawtio-4.2.0

RUN curl -Ls https://sh.jbang.dev | bash -s - app setup \
    && source ~/.bashrc \
    && jbang version --update \
    && jbang trust add https://github.com/hawtio/hawtio/ \
    && jbang app install hawtio@hawtio/hawtio/$HAWTIO_REPOS_TAG

ENV PATH=/home/default/.jbang/bin:$PATH

ADD --chown=default --chmod=744 entrypoint.sh /home/default/

ENTRYPOINT ["/home/default/entrypoint.sh"]


