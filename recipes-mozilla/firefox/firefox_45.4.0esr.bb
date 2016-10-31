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

SRC_URI[archive.md5sum] = "20358acfbb9e11782940c180fd2b1528"
SRC_URI[archive.sha256sum] = "cfd90096b9e1019b9de4fe061ece8c65f668b8a24bcbb657ce6b3c940ef83ad0"

PR = "r2"
S = "${WORKDIR}/firefox-45.4.0esr"
# MOZ_APP_BASE_VERSION should be incremented after a release
MOZ_APP_BASE_VERSION = "45.4.0"

inherit mozilla

export MOZCONFIG = "${WORKDIR}/mozconfig-45esr"

EXTRA_OEMAKE += "installdir=${libdir}/${PN}-${MOZ_APP_BASE_VERSION}"

ARM_INSTRUCTION_SET = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)}"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3,--enable-default-toolkit=cairo-gtk2,gtk+3,"
PACKAGECONFIG[glx] = ",,,"
PACKAGECONFIG[egl] = "--with-gl-provider=EGL,,virtual/egl,"
PACKAGECONFIG[gstreamer1.0] = "--enable-gstreamer=1.0,--disable-gstreamer,gstreamer1.0,libgstvideo-1.0 gstreamer1.0-plugins-base-app"
PACKAGECONFIG[openmax] = ",,,"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland', \
           'file://wayland-patches/0001-Initial-patch-from-https-stransky.fedorapeople.org-f.patch \
            file://wayland-patches/0002-gdk_x11_get_server_time-fix.patch \
            file://wayland-patches/0003-Fixed-gdk_x11_get_server_time-for-wayland.patch \
            file://wayland-patches/0004-Install-popup_take_focus_filter-to-actual-GdkWindow.patch \
            file://wayland-patches/0005-Fixed-nsWindow-GetLastUserInputTime.patch \
            file://wayland-patches/0008-GLLibraryEGL-Use-wl_display-to-get-EGLDisplay-on-Way.patch \
            file://wayland-patches/0009-Use-wl_egl_window-as-a-native-EGL-window-on-Wayland.patch \
            file://wayland-patches/0010-Disable-query-EGL_EXTENSIONS.patch \
            file://wayland-patches/0011-Wayland-Detect-existence-of-wayland-libraries.patch \
            file://wayland-patches/0012-Add-AC_TRY_LINK-for-libwayland-egl.patch \
            file://wayland-patches/0013-Wayland-Resize-wl_egl_window-when-the-nsWindow-is-re.patch \
           ', \
           '', d)}"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', \
           'file://gpu.js', '', d)}"

# Current EGL patch for Wayland doesn't work well on windowed mode.
# To avoid this issue, force use fullscreen mode by default.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', \
           'file://wayland-patches/frameless.patch', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           'file://openmax/0001-Update-dom-media-platforms-omx-to-support-video-deco.patch \
            file://openmax/0002-Add-the-initial-implementation-of-PureOmxPlatformLay.patch \
            file://openmax/0003-PDMFactory-Add-a-fallback-blank-decoder-module.patch \
            file://openmax/openmax.js \
           ', \
           '', d)}"

python do_check_variables() {
    if bb.utils.contains('PACKAGECONFIG', 'glx egl', True, False, d):
        bb.warn("%s: GLX support will be disabled when EGL is enabled!" % bb.data.getVar('PN', d, 1))
}
addtask check_variables before do_configure

do_install_append() {
    install -d ${D}${datadir}/applications
    install -d ${D}${datadir}/pixmaps

    install -m 0644 ${WORKDIR}/mozilla-firefox.desktop ${D}${datadir}/applications/
    install -m 0644 ${WORKDIR}/mozilla-firefox.png ${D}${datadir}/pixmaps/
    install -m 0644 ${WORKDIR}/vendor.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/gpu.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/openmax/openmax.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
    fi

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
                libplds4.so \
                liblgpllibs.so \
                libmozgtk.so"

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
