# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio icu nodejs-native cbindgen-native \
            yasm-native nasm-native unzip-native \
            virtual/${TARGET_PREFIX}rust cargo-native ${RUSTLIB_DEP} \
           "
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=33;md5=35d7fa1c4b86c115051c925fd624a5be"

#SRC_URI = "git://github.com/mozilla/gecko-dev.git;branch=master \
#

SRC_URI = "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozconfig \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://prefs/vendor.js \
           file://fixes/enable-to-specify-rust-target.patch \
           file://fixes/0001-libloading-Use-lazy_static-instead-of-weak-static.patch \
           file://fixes/link-with-libpangoft.patch \
           file://fixes/fix-get-cpu-feature-definition-conflict.patch \
           file://fixes/fix-camera-permission-dialg-doesnot-close.patch \
           file://wayland/bug1451816-workaround-for-grabbing-popup.patch \
           file://wayland/egl/0001-Disable-query-EGL_EXTENSIONS.patch \
           file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
           file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
           "
SRC_URI_append_libc-musl = "\
           file://musl/musl-mutex.patch \
           file://musl/musl_webrtc_glibcism.patch \
           file://musl/fix-bug-1261392.patch \
           file://musl/musl-tools-fix.patch \
           file://musl/musl-cmsghdr.patch \
"

SRC_URI[archive.md5sum] = "4ec5ac732a8bc3b210aab461928a2193"
SRC_URI[archive.sha256sum] = "8e74f6b3b666f64cb0009d7e9065639bf9f959cb916202765eed346cc712397b"
S = "${WORKDIR}/firefox-${MOZ_APP_BASE_VERSION}"

#SRCREV = "${AUTOREV}"
#SRCREV = "18214ad3bf0816e79da0830b67ceeec641efbebe"
#S = "${WORKDIR}/git"

#MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"
MOZ_APP_BASE_VERSION = "68.0"

inherit mozilla rust-common

DISABLE_STATIC=""

ARM_INSTRUCTION_SET_armv5 = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,--enable-default-toolkit=cairo-gtk3,virtual/egl,"
PACKAGECONFIG[gpu] = ",,,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"
PACKAGECONFIG[disable-e10s] = ",,,"
PACKAGECONFIG[forbit-multiple-compositors] = ",,,"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'gpu', \
           'file://prefs/gpu.js', '', d)}"

# Additional upstream patches to support OpenMAX IL
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           'file://prefs/openmax.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'webgl', \
           'file://prefs/webgl.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'disable-e10s', \
           'file://prefs/disable-e10s.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', \
           'file://prefs/single-compositor.js \
            file://fixes/0001-Enable-to-suppress-multiple-compositors.patch \
	   ', '', d)}"

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/prefs/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'gpu', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'disable-e10s', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/disable-e10s.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
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
                    ${libdir}/${PN}/fix_linux_stack.py \
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
    libmozavcodec.so \
    libmozavutil.so \
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
