# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio yasm-native icu \
           "
#            virtual/${TARGET_PREFIX}rust"
#            rust-native rust-cross-${TARGET_ARCH} cargo-native libstd-rs"
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=32;md5=9ccc4b003ce6a52174487ee0ea8085ee"

SRC_URI = "git://github.com/mozilla/gecko-dev.git;branch=master \
           file://mozconfig \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://vendor.js \
           file://autoconfig.js \
           file://autoconfig.cfg \
           file://rustc_target_force.patch \
           file://wayland/gem/0001-Permit-to-use-gtk-wayland-3.0-3.18.patch \
           file://fixes/0001-Fix-a-build-error-of-Gecko-Profiler-for-Linux-ARM.patch \
           "

#FIXME: Set exact source revision
SRCREV = "${AUTOREV}"

PR = "r0"
S = "${WORKDIR}/git"
MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla
#inherit rust-common

DISABLE_STATIC=""
EXTRA_OEMAKE += "installdir=${libdir}/${PN}-${MOZ_APP_BASE_VERSION}"

ARM_INSTRUCTION_SET = "arm"

# FIXME: They won't be applied because do_configure is overriden
PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,"
PACKAGECONFIG[glx] = ",,,"
PACKAGECONFIG[egl] = "--with-gl-provider=EGL,,virtual/egl,"
PACKAGECONFIG[openmax] = ",,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[canvas-gpu] = ",,,"

# Stransky's wayland patches
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland', \
           ' \
           ', \
           '', d)}"

# Gecko Embedded's Additional wayland patches to support EGL
#
# Current EGL patches doesn't work well on windowed mode.
# To avoid this issue, force use fullscreen mode.
# In addition, e10s (multi process window) isn't also supported yet.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', \
           ' \
            file://e10s.js \
           ', \
           '', d)}"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', \
           'file://gpu.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           ' \
            file://openmax/openmax.js \
           ', \
           '', d)}"

SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'webgl', \
           'file://webgl.js', '', d)}"

SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'canvas-gpu', \
           'file://canvas-gpu.js', '', d)}"

python do_check_variables() {
    if bb.utils.contains('PACKAGECONFIG', 'glx egl', True, False, d):
        bb.warn("%s: GLX support will be disabled when EGL is enabled!" % bb.data.getVar('PN', d, 1))
    if bb.utils.contains_any('PACKAGECONFIG', 'glx egl', False, True, d):
        if bb.utils.contains('PACKAGECONFIG', 'webgl', True, False, d):
            bb.warn("%s: WebGL won't be enabled when both glx and egl aren't enabled!" % bb.data.getVar('PN', d, 1))
        if bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', True, False, d):
            bb.warn("%s: Canvas acceleration won't be enabled when both glx and egl aren't enabled!" % bb.data.getVar('PN', d, 1))
}
addtask check_variables before do_configure

do_dump_env(){
    LOGFILE="/var/tmp/firefox/env.txt"

    if [ -e `dirname ${LOGFILE}` ]; then
        if [ -e ${LOGFILE} ]; then
            rm             ${LOGFILE}
        fi
    else
        mkdir -p           `dirname ${LOGFILE}`
    fi

    echo ${RUSTC}                       >> ${LOGFILE}
    echo ""                             >> ${LOGFILE}

    echo ${RUST_BUILD_SYS}              >> ${LOGFILE}
    echo ${RUST_BUILD_CC}               >> ${LOGFILE}
    echo ${RUST_BUILD_CXX}              >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${RUST_HOST_SYS}               >> ${LOGFILE}
    echo ${RUST_HOST_CC}                >> ${LOGFILE}
    echo ${RUST_HOST_CXX}               >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${RUST_TARGET_SYS}             >> ${LOGFILE}
    echo ${RUST_TARGET_CC}              >> ${LOGFILE}
    echo ${RUST_TARGET_CXX}             >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo `pwd`                          >> ${LOGFILE}
    echo ${S}                           >> ${LOGFILE}
    echo ${WORKDIR}                     >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${STAGING_BINDIR}              >> ${LOGFILE}
    echo ${STAGING_BINDIR_CROSS}        >> ${LOGFILE}
    echo ${STAGING_BINDIR_NATIVE}       >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${bindir}                      >> ${LOGFILE}
    echo ${BINDIR}                      >> ${LOGFILE}
    echo ${BUILD_PREFIX}                >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${CC}                          >> ${LOGFILE}
    echo ${LD}                          >> ${LOGFILE}
    echo ${CXX}                         >> ${LOGFILE}
    echo ${CFLAGS}                      >> ${LOGFILE}
    echo ${CXXFLAGS}                    >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${HOST_CC}                     >> ${LOGFILE}
    echo ${HOST_LD}                     >> ${LOGFILE}
    echo ${HOST_CXX}                    >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${BUILD_ARCH}                  >> ${LOGFILE}
    echo ${BUILD_OS}                    >> ${LOGFILE}
    echo ${BUILD_SYS}                   >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${HOST_ARCH}                   >> ${LOGFILE}
    echo ${HOST_OS}                     >> ${LOGFILE}
    echo ${HOST_SYS}                    >> ${LOGFILE}

    echo ""                             >> ${LOGFILE}
    echo ${TARGET_ARCH}                 >> ${LOGFILE}
    echo ${TARGET_OS}                   >> ${LOGFILE}
    echo ${TARGET_SYS}                  >> ${LOGFILE}
    echo ${TARGET_PREFIX}               >> ${LOGFILE}
}
addtask do_dump_env before do_configure

do_configure() {
    export SHELL=/bin/bash
    export RUST_TARGET_PATH=${STAGING_LIBDIR_NATIVE}/rustlib

    ./mach configure \
            #--target=${TARGET_SYS}
            #--host=${HOST_SYS} \
            #--host=${RUST_HOST_SYS} \
            #--target=${RUST_TARGET_SYS}
            #--host="${HOST_ARCH}-unknown-${HOST_OS}" \
            #--target="${TARGET_ARCH}-unknown-${TARGET_OS}"
}

do_compile() {
    export SHELL="/bin/bash"
    export RUST_TARGET_PATH=${STAGING_LIBDIR_NATIVE}/rustlib

    ./mach build \
            #--target=${TARGET_SYS}
            #--host=${HOST_SYS} \
}

do_install() {
    export SHELL="/bin/bash"
    INSTALL_SDK=0 DESTDIR="${D}" ./mach install
}

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    install -m 0644 ${WORKDIR}/autoconfig.js ${D}${libdir}/${PN}/defaults/pref/
    install -m 0644 ${WORKDIR}/autoconfig.cfg ${D}${libdir}/${PN}/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/openmax/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/e10s.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/canvas-gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi

    # Fix ownership of files
    chown root:root -R ${D}${datadir}
    chown root:root -R ${D}${libdir}
}

FILES_${PN} = "${bindir}/${PN} \
               ${datadir}/applications/ \
               ${datadir}/pixmaps/ \
               ${libdir}/${PN}/* \
               ${libdir}/${PN}/.autoreg \
               ${bindir}/defaults"
FILES_${PN}-dev += "${datadir}/idl ${bindir}/${PN}-config ${libdir}/${PN}-devel-*"
FILES_${PN}-staticdev += "${libdir}/${PN}-devel-*/sdk/lib/*.a"
FILES_${PN}-dbg += "${libdir}/${PN}/.debug \
                    ${libdir}/${PN}/*/.debug \
                    ${libdir}/${PN}/*/*/.debug \
                    ${libdir}/${PN}/*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/.debug \
                    ${libdir}/${PN}-devel-*/*/*/*/.debug \
                    ${bindir}/.debug"

# We don't build XUL as system shared lib, so we can mark all libs as private
PRIVATE_LIBS = " \
    libmozjs.so \
    libxpcom.so \
    libnspr4.so \
    libxul.so \
    libmozalloc.so \
    libplc4.so \
    libplds4.so \
    liblgpllibs.so \
    libmozgtk.so \
    libmozwayland.so \
    libmozsqlite3.so \
    libclearkey.so \
"

# mark libraries also provided by nss as private too
PRIVATE_LIBS += " \
    libfreebl3.so \
    libfreeblpriv3.so \
    libnss3.so \
    libnssckbi.so \
    libsmime3.so \
    libnssutil3.so \
    libnssdbm3.so \
    libssl3.so \
    libsoftokn3.so \
"
