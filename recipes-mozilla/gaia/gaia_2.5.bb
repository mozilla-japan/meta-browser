SUMMARY = "HTML5-based Phone UI for the Boot 2 Gecko Project"

PR = "r1"

LIC_FILES_CHKSUM = "file://LICENSE;md5=7eca70cd144bd72119f935f821f4f922"

LAUNCHER_NAME = "b2g"

SRCREV = "095ece02e568eea316e7ee828f34da748066e6c9"
SRC_URI = "git://github.com/mozilla-b2g/gaia.git;branch=v2.5 \
           file://${LAUNCHER_NAME}.png \
           file://${LAUNCHER_NAME}.desktop \
           file://${LAUNCHER_NAME}.sh \
           "

export GAIA_DEVICE_TYPE = "phone"
export GAIA_APP_TARGET = "production"

inherit gaia

do_install_append() {
    install -d ${D}${bindir}
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0755 ${WORKDIR}/${LAUNCHER_NAME}.sh ${D}${bindir}/${LAUNCHER_NAME}
    install -m 0644 ${WORKDIR}/${LAUNCHER_NAME}.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/${LAUNCHER_NAME}.png ${D}${datadir}/pixmaps/
}
