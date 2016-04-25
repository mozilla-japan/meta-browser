# Based on firefox_38.6.1esr.bb:
#   Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
#   Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Boot to Gecko aims to create a complete, standalone operating system for the open web."
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify \
            virtual/libgl nspr pulseaudio yasm-native icu"

LICENSE = "MPLv2 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRCREV = "df9367456ebfe9ed1ce3501886416e2046029f52"
SRC_URI = "git://github.com/mozilla/gecko-dev.git;branch=b2g44_v2_5 \
           file://fix-python-path.patch \
           file://0001-Fix-a-broken-build-option-with-gl-provider.patch \
           file://0002-Fix-a-build-error-on-enabling-both-Gtk-2-and-EGL.patch \
           "

MOZ_APP_BASE_VERSION = "44.0"

S = "${WORKDIR}/git"

inherit mozilla

PACKAGES = "${PN}-dbg ${PN}"

ARM_INSTRUCTION_SET = "arm"

do_install() {
    oe_runmake -f client.mk package
    install -d ${D}${libdir}
    tar xvfj ${MOZ_OBJDIR}/dist/${PN}-${MOZ_APP_BASE_VERSION}.en-US.linux-gnueabi-arm.tar.bz2 -C ${D}${libdir}
}

FILES_${PN} = "${libdir}/${PN}/"
FILES_${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                   "

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = " \
    libmozjs.so \
    libxpcom.so \
    libnspr4.so \
    libxul.so \
    libmozalloc.so \
    libplc4.so \
    libplds4.so \
    libmozsqlite3.so \
"

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

PRIVATE_LIBS += " \
    libdbusservice.so \
    libclearkey.so \
"
