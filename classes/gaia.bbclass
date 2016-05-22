LICENSE = "Apache-2.0"
RDEPENDS_${PN} = "b2g"

PACKAGES = "${PN}"

EXTRA_OEMAKE += "SHELL=/bin/sh"

export GAIA_DEVICE_TYPE ?= "phone"
export PROFILE_FOLDER ?= "profile-${GAIA_DEVICE_TYPE}"
export NOFTU ?= "1"
export NOFTUPING ?= "1"
export NO_LOCK_SCREEN ?= "1"
export SCREEN_TIMEOUT ?= "0"

S = "${WORKDIR}/git"

GAIA_COREAPPSDIR_PREF = "user_pref('b2g.coreappsdir', \"${libdir}/${PN}\");"

gaia_do_compile() {
    oe_runmake b2g_sdk
    oe_runmake
    echo "${GAIA_COREAPPSDIR_PREF}" >> ${S}/${PROFILE_FOLDER}/user.js
    echo "${GAIA_COREAPPSDIR_PREF}" >> ${S}/${PROFILE_FOLDER}/defaults/pref/user.js
}

gaia_do_install() {
    install -d ${D}${libdir}/${PN}
    install -d ${D}${datadir}/${PN}
    tar cvfz ${PROFILE_FOLDER}.tar.gz ${PROFILE_FOLDER} --exclude webapps -C ${S}
    cp -r ${S}/${PROFILE_FOLDER}/webapps ${D}${libdir}/${PN}
    install -m 0644 ${S}/${PROFILE_FOLDER}.tar.gz ${D}${datadir}/${PN}
}

EXPORT_FUNCTIONS do_compile do_install
