# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio icu dbus-glib \
            nodejs-native cbindgen-native \
            yasm-native nasm-native unzip-native \
            virtual/${TARGET_PREFIX}rust cargo-native ${RUSTLIB_DEP} \
           "
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=33;md5=b186c4b15b6099eac08a112f6243ee46"

CVE_PRODUCT = "mozilla:firefox"

SRC_URI = "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozconfig \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://prefs/vendor.js \
           file://fixes/bug1545437-enable-to-specify-rust-target.patch \
           file://fixes/avoid-running-autoconf2.13.patch \
           file://fixes/pre-generated-old-configure.patch \
           file://fixes/link-with-libpangoft.patch \
           file://fixes/fix-camera-permission-dialg-doesnot-close.patch \
           file://fixes/Allow-.js-preference-files-to-set-locked-prefs-with-.patch \
           file://fixes/Bug-1650299-Unify-the-inclusion-of-the-ICU-data-file.patch \
           file://fixes/Bug-1264836-Automatically-convert-the-little-endian-.patch \
           file://fixes/Bug-1526653-Include-struct-definitions-for-user_vfp-.patch \
           file://fixes/0001-rust-target-lexicon-0.9.0-Add-Poky-to-Vendor.patch \
           file://fixes/0002-Don-t-include-dependency-flags-in-HOST_CFLAGS-for-ru.patch \
           file://fixes/0003-rust-autocfg-0.1.6-Don-t-specify-target-for-rustc.patch \
           file://fixes/0001-Add-a-preference-to-force-enable-touch-events-withou.patch \
           file://porting/Add-xptcall-support-for-SH4-processors.patch \
           file://porting/NSS-Fix-FTBFS-on-Hurd-because-of-MAXPATHLEN.patch \
           file://porting/Work-around-Debian-bug-844357.patch \
           file://porting/Use-NEON_FLAGS-instead-of-VPX_ASFLAGS-for-libaom-neo.patch \
           file://porting/Work-around-GCC-ICE-on-mips-i386-and-s390x.patch \
           file://porting/Work-around-another-GCC-ICE-on-arm.patch \
           file://prefs/Set-javascript.options.showInConsole.patch \
           file://prefs/Set-DPI-to-system-settings.patch \
           file://prefs/Don-t-auto-disable-extensions-in-system-directories.patch \
           file://debian-hacks/Avoid-wrong-sessionstore-data-to-keep-windows-out-of.patch \
           file://debian-hacks/Add-another-preferences-directory-for-applications-p.patch \
           file://debian-hacks/Don-t-register-plugins-if-the-MOZILLA_DISABLE_PLUGIN.patch \
           file://debian-hacks/Add-a-2-minutes-timeout-on-xpcshell-tests.patch \
           file://debian-hacks/Don-t-build-image-gtests.patch \
           file://debian-hacks/Set-program-name-from-the-remoting-name.patch \
           file://debian-hacks/Use-the-Mozilla-Location-Service-key-when-the-Google.patch \
           file://debian-hacks/Avoid-using-vmrs-vmsr-on-armel.patch \
           file://debian-hacks/Use-remoting-name-for-call-to-gdk_set_program_class.patch \
           file://debian-hacks/Use-build-id-as-langpack-version-for-reproducibility.patch \
           file://debian-hacks/Don-t-build-ICU-in-parallel.patch \
           file://debian-hacks/Allow-to-build-with-older-versions-of-nodejs-10.patch \
           file://wayland/egl/bug1571603-Disable-eglQueryString-nullptr-EGL_EXTENSIONS.patch \
           file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
           file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
           file://wayland/firefox-wayland.sh \
           "

SRC_URI[archive.md5sum] = "0aa9c735305304373f9fddc35c56e81b"
SRC_URI[archive.sha256sum] = "c41f45072b0eb84b9c5dcb381298f91d49249db97784c7e173b5f210cd15cf3f"

S = "${WORKDIR}/firefox-${MOZ_APP_BASE_VERSION}"

MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla rust-common

TOOLCHAIN_pn-firefox = "clang"
AS = "${CC}"

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
PACKAGECONFIG[forbit-multiple-compositors] = ",,,"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'gpu', \
           'file://prefs/gpu.js', '', d)}"

# Additional upstream patches to support OpenMAX IL
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           'file://fixes/Bug-1590977-openmax-Import-latest-OpenMAX-IL-1.1.2-headers.patch \
            file://prefs/openmax.js \
           ', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'webgl', \
           'file://prefs/webgl.js', '', d)}"

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
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'forbit-multiple-compositors', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'wayland', '1', '', d)}" ]; then
        install -d ${D}${sysconfdir}/profile.d
        install -m 0755 ${WORKDIR}/wayland/firefox-wayland.sh ${D}${sysconfdir}/profile.d/
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
               ${bindir}/defaults \
               ${sysconfdir}/profile.d/*"
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

CLEANBROKEN = "1"
