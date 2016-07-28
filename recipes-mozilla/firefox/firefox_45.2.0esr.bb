# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio yasm-native icu"

LICENSE = "MPLv2 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://vendor.js \
           file://fix-python-path.patch \
           file://0001-Fix-a-broken-build-option-with-gl-provider.patch \
           file://0002-Fix-a-build-error-on-enabling-both-Gtk-2-and-EGL.patch \
           file://mozconfig-45esr \
           "

SRC_URI[archive.md5sum] = "709f3b0936e78c16e5e913ba518190da"
SRC_URI[archive.sha256sum] = "1a729774034231c919dc5a556e17d3342792d5347c755d8d0a4f67a07374804b"

PR = "r0"
S = "${WORKDIR}/firefox-45.2.0esr"
# MOZ_APP_BASE_VERSION should be incremented after a release
MOZ_APP_BASE_VERSION = "45.2.0"

inherit mozilla

export MOZCONFIG = "${WORKDIR}/mozconfig-45esr"

EXTRA_OEMAKE += "installdir=${libdir}/${PN}-${MOZ_APP_BASE_VERSION}"

ARM_INSTRUCTION_SET = "arm"

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/vendor.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
    chown root:root -R ${D}${libdir}
}

FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/* \
               ${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/.autoreg \
               ${bindir}/defaults"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/.debug \
                    ${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/*/.debug \
                    ${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/*/*/.debug \
                    ${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${bindir}/.debug"

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = "libmozjs.so \
                libxpcom.so \
                libnspr4.so \
                libxul.so \
                libmozalloc.so \
                libplc4.so \
                libplds4.so"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"
