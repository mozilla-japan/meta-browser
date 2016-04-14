SUMMARY = "HTML5-based Phone UI for the Boot 2 Gecko Project"
LICENSE = "Apache-2.0"
RDEPENDS_${PN} = "b2g"

LIC_FILES_CHKSUM = "file://LICENSE;md5=7eca70cd144bd72119f935f821f4f922"

SRCREV = "095ece02e568eea316e7ee828f34da748066e6c9"
SRC_URI = "git://github.com/mozilla-b2g/gaia.git;branch=v2.5 \
           file://b2g.png \
           file://b2g.desktop \
           file://b2g.sh \
           "

PACKAGES = "${PN}"

EXTRA_OEMAKE += "SHELL=/bin/sh"

export GAIA_DEVICE_TYPE = "phone"
export NOFTU = "1"
export NO_LOCK_SCREEN = "1"
export SCREEN_TIMEOUT = "0"

S = "${WORKDIR}/git"

GAIA_COREAPPSDIR_PREF = "user_pref('b2g.coreappsdir', \"${libdir}/${PN}\");"

do_compile() {
    oe_runmake b2g_sdk
    oe_runmake
    echo "${GAIA_COREAPPSDIR_PREF}" >> ${S}/profile/user.js
    echo "${GAIA_COREAPPSDIR_PREF}" >> ${S}/profile/defaults/pref/user.js
}

do_install() {
    install -d ${D}${libdir}/${PN}
    install -d ${D}${datadir}/${PN}
    tar cvfz profile.tar.gz profile --exclude webapps -C ${S}
    cp -r ${S}/profile/webapps ${D}${libdir}/${PN}
    install -m 0644 ${S}/profile.tar.gz ${D}${datadir}/${PN}

    install -d ${D}${bindir}
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps
    install -m 0755 ${WORKDIR}/b2g.sh ${D}${bindir}/b2g
    install -m 0644 ${WORKDIR}/b2g.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/b2g.png ${D}${datadir}/pixmaps/
}
