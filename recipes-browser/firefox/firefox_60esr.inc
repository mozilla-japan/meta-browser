# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

export APP_NAME = "${PN}"

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio yasm-native icu unzip-native \
            virtual/${TARGET_PREFIX}rust cargo-native ${RUSTLIB_DEP} \
           "
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=33;md5=f51d0fbc370c551d7371775b4f6544ca"

FILESEXTRAPATHS_prepend := "${THISDIR}/firefox:"

SRC_URI = "https://ftp.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozconfig \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://prefs/vendor.js \
           file://prefs/autoconfig.js \
           file://prefs/autoconfig.cfg \
           file://fixes/bug1433081-fix-with-gl-provider-option.patch \
           file://fixes/bug1434526-Fix-a-build-error-of-Gecko-Profiler.patch \
           file://fixes/bug1479540-Accept-triplet-strings-with-only-two.patch \
           file://fixes/0001-Enable-to-specify-RUST_TARGET-via-enviroment-variabl.patch \
           file://fixes/rustc_cross_flags.patch \
           file://fixes/0001-Add-clang-s-include-path-on-cross-compiling.patch \
           file://fixes/0001-Add-a-preference-to-force-enable-touch-events-withou.patch \
           file://fixes/fix-get-cpu-feature-definition-conflict.patch \
           file://fixes/fix-camera-permission-dialg-doesnot-close.patch \
           file://fixes/0001-Inject-configurable-reftest-wait-class-script-in-mar.patch \
           file://gn-configs/x64_False_arm64_linux.json \
           file://gn-configs/x64_False_arm_linux.json \
           "

PR = "r0"
MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"
S = "${WORKDIR}/firefox-${MOZ_APP_BASE_VERSION}"

inherit mozilla rust-common

DISABLE_STATIC=""
EXTRA_OEMAKE += "installdir=${libdir}/${PN}"

ARM_INSTRUCTION_SET_armv5 = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "alsa", "alsa", "", d)} \
                   ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)} \
                   ${@bb.utils.contains_any("TARGET_ARCH", "x86_64 arm aarch64", "webrtc", "", d)} \
"
PACKAGECONFIG[alsa] = "--enable-alsa,--disable-alsa,alsa-lib"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,"
PACKAGECONFIG[glx] = ",,,"
PACKAGECONFIG[egl] = "--with-gl-provider=EGL,,virtual/egl,"
PACKAGECONFIG[openmax] = "--enable-openmax,,,"
PACKAGECONFIG[webgl] = ",,,"
PACKAGECONFIG[canvas-gpu] = ",,,"
PACKAGECONFIG[stylo] = "--enable-stylo,--disable-stylo,,"
PACKAGECONFIG[webrtc] = "--enable-webrtc,--disable-webrtc,,"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland', \
           ' \
            file://wayland/bug1468670-enable-alt-modifier-on-wayland.patch \
            file://wayland/bug1438131-Implement-Drop-on-Wayland.patch \
            file://wayland/bug1460810-fix-segfault-while-pasting-text.patch \
            file://wayland/bug1438136-clipboard-text-null-terminate.patch \
            file://wayland/bug1461306-fix-size-of-mime-type-array.patch \
            file://wayland/bug1462622-Dont-use-GLXVsyncSource-on-non-X11-displays.patch \
            file://wayland/bug1462640-Allow-content-processes-to-mincore.patch \
            file://wayland/bug1464808-Set-move-as-default-Drag-Drop-action.patch \
            file://wayland/bug1451816-workaround-for-grabbing-popup.patch \
            file://wayland/0001-Permit-to-use-gtk-wayland-3.0-3.18.patch \
            file://wayland/0001-Add-workarround-to-reduce-unexpected-window-resize-o.patch \
            file://wayland/bug1504689-Wayland-Clipboard-Don-t-delete-clipboard.patch \
           ', \
           '', d)}"

# Gecko Embedded's Additional wayland patches to support EGL
# On RZ/G1, two or more EGL window is not supported.
# Thus, e10s should be disabled when EGL is enabled.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', \
           ' \
            file://wayland/egl/bug1460603-GLLibraryEGL-Use-wl_display-to-get-EGLDisplay-on-Way.patch \
            file://wayland/egl/bug1460605-Provide-NS_NATIVE_EGL_WINDOW-to-get-a-native-EGL-window-on-wa.patch \
            file://wayland/egl/bug1460605-Use-NS_NATIVE_EGL_WINDOW-instead-of-NS_NATIVE_WINDOW-on-GTK.patch \
            file://wayland/egl/bug1374136-Enable-sharing-SharedSurface_EGLImage.patch \
            file://wayland/egl/bug1462642-Use-dummy-wl_egl_window-instead-of-PBuffer.patch \
            file://wayland/egl/bug1464823-avoid-freeze-on-starting-compositor.patch \
            file://wayland/egl/0001-GLLibraryLoader-Use-given-symbol-lookup-function-fir.patch \
            file://wayland/egl/0002-Disable-query-EGL_EXTENSIONS.patch \
            file://wayland/egl/0001-Mark-GLFeature-framebuffer_multisample-as-unsupporte.patch \
            file://wayland/egl/Observe-didcomposite.patch \
            file://wayland/egl/bug1489902-Use-wl_callback_listener-instead-of-after-paint-even.patch \
            file://prefs/disable-e10s.js \
           ', \
           '', d)}"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', \
           '\
            file://prefs/gpu.js \
            file://prefs/single-compositor.js \
            file://fixes/suppress-multiple-compositors.patch \
	   ', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           ' \
            file://openmax/0001-Add-initial-implementation-of-PureOmxPlatformLayer.patch \
            file://openmax/0002-OmxDataDecoder-Fix-a-stall-issue-on-shutting-down.patch \
            file://openmax/0003-Plug-memory-leak-of-PureOmxPlatformLayer.patch \
            file://openmax/0004-Dont-test-OMX_UseEGLImage.patch \
            file://prefs/openmax.js \
           ', \
           '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'webgl', \
           'file://prefs/webgl.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', \
           'file://prefs/canvas-gpu.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'kiosk', \
           ' \
            file://wayland/kiosk/0001-Make-the-window-fullscreen-by-default.patch \
           ', \
           '', d)}"

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

do_configure() {
    export SHELL=/bin/bash
    export RUSTSTDLIB="${STAGING_LIBDIR}/rust"
    export RUSTFLAGS="${RUSTFLAGS} -Cpanic=unwind"
    export RUST_TARGET="${TARGET_SYS}"
    export RUST_TARGET_PATH=${STAGING_LIBDIR_NATIVE}/rustlib
    export BINDGEN_MFLOAT="${@bb.utils.contains('TUNE_CCARGS_MFLOAT', 'hard', '-mfloat-abi=hard', '', d)}"
    export BINDGEN_CFLAGS="--target=${TARGET_SYS} --sysroot=${RECIPE_SYSROOT} ${BINDGEN_MFLOAT}"

    # We still need to support Yocto 2.1.
    # After migrating Yocto 2.4, it can be removed.
    export FALLBACK_CONFIGURE_ARGS="${CONFIGURE_ARGS}"
    ./mach configure ${PACKAGECONFIG_CONFARGS} ${FALLBACK_CONFIGURE_ARGS}
    cp ${WORKDIR}/gn-configs/*.json ${S}/media/webrtc/gn-configs/
    ./mach build-backend -b GnMozbuildWriter
}

do_compile() {
    export SHELL="/bin/bash"
    export RUST_TARGET_PATH=${STAGING_LIBDIR_NATIVE}/rustlib

    ./mach build
}

do_install() {
    export SHELL="/bin/bash"

    INSTALL_SDK=0 DESTDIR="${D}" ./mach install
}

do_install_append() {
    install -m 0644 ${WORKDIR}/prefs/vendor.js ${D}${libdir}/${PN}/defaults/pref/
    install -m 0644 ${WORKDIR}/prefs/autoconfig.js ${D}${libdir}/${PN}/defaults/pref/
    install -m 0644 ${WORKDIR}/prefs/autoconfig.cfg ${D}${libdir}/${PN}/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/gpu.js ${D}${libdir}/${PN}/defaults/pref/
        install -m 0644 ${WORKDIR}/prefs/single-compositor.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/openmax.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'webgl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/webgl.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'canvas-gpu', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/canvas-gpu.js ${D}${libdir}/${PN}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/prefs/disable-e10s.js ${D}${libdir}/${PN}/defaults/pref/
    fi

    # Fix ownership of files
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
