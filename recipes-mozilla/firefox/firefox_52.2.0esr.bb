# Copyright (C) 2009-2015, O.S. Systems Software Ltda. All Rights Reserved
# Released under the MIT license (see packages/COPYING)

DESCRIPTION ?= "Browser made by mozilla"
DEPENDS += "alsa-lib curl startup-notification libevent cairo libnotify \
            virtual/libgl pulseaudio yasm-native icu"
RDEPENDS_${PN}-dev = "dbus"

LICENSE = "MPLv2 | GPLv2+ | LGPLv2.1+"
LIC_FILES_CHKSUM = "file://toolkit/content/license.html;endline=39;md5=f7e14664a6dca6a06efe93d70f711c0e"

SRC_URI = "https://archive.mozilla.org/pub/firefox/releases/${PV}/source/firefox-${PV}.source.tar.xz;name=archive \
           file://mozilla-firefox.png \
           file://mozilla-firefox.desktop \
           file://vendor.js \
           file://autoconfig.js \
           file://autoconfig.cfg \
           file://avoid-running-config-status.patch \
           file://remove-needless-windows-dependency.patch \
           file://0041-Fix-a-broken-build-option-with-gl-provider.patch \
           file://0042-Fix-a-build-error-on-enabling-both-Gtk-2-and-EGL.patch \
           file://firefox-50-fix-build-error-without-glx.patch \
           file://0001-Add-a-preference-to-force-enable-touch-events-withou.patch \
           file://mozconfig \
           "

SRC_URI[archive.md5sum] = "512594d84c9aec3fb094eb4bba10e441"
SRC_URI[archive.sha256sum] = "a2f180e4109b15d86d58444134996c1d49eb52e7702d89510508fbd7bddb9381"

PR = "r0"
S = "${WORKDIR}/firefox-${PV}"
MOZ_APP_BASE_VERSION = "${@'${PV}'.replace('esr', '')}"

inherit mozilla

DISABLE_STATIC=""
EXTRA_OEMAKE += "installdir=${libdir}/${PN}-${MOZ_APP_BASE_VERSION}"

ARM_INSTRUCTION_SET = "arm"

PACKAGECONFIG ??= "${@bb.utils.contains("DISTRO_FEATURES", "wayland", "wayland", "", d)}"
PACKAGECONFIG[wayland] = "--enable-default-toolkit=cairo-gtk3-wayland,"
PACKAGECONFIG[glx] = ",,,"
PACKAGECONFIG[egl] = "--with-gl-provider=EGL,,virtual/egl,"
PACKAGECONFIG[openmax] = ",,,"

# Stransky's wayland patches
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland', \
           'file://wayland-patches/0001-Added-wayland-patch.patch \
            file://wayland-patches/0002-build-fix.patch \
            file://wayland-patches/0003-Debug-version.patch \
            file://wayland-patches/0004-Removed-debug-Dump-code.patch \
            file://wayland-patches/0005-Dynamically-resize-wl_buffer-according-to-attached-w.patch \
            file://wayland-patches/0006-fixed-rendering-via.-frame-callback.patch \
            file://wayland-patches/0007-Create-.mozconfig.patch \
            file://wayland-patches/0008-Fixed-flickering-when-wl_buffer-is-altered.patch \
            file://wayland-patches/0009-Added-wayland-lib-wrapper.patch \
            file://wayland-patches/0010-Fixed-CurrentX11TimeGetter-usage-fixed-WindowSurface.patch \
            file://wayland-patches/0011-Fixed-timestamps.patch \
            file://wayland-patches/0012-Import-updated-mozcontainer.cpp-gfxPlatform.cpp-patc.patch \
            file://wayland-patches/0013-fixed-crash-at-browser-end.patch \
            file://wayland-patches/0014-Removed-wayland-client-from-libxul.so.patch \
            file://wayland-patches/0015-Removed-unused-code.patch \
            file://wayland-patches/0016-Removed-NS_NATIVE_COMPOSITOR_DISPLAY_X11.patch \
            file://wayland-patches/0017-Link-wayland-run-time-and-provide-fallback-library-w.patch \
            file://wayland-patches/0018-Added-clipboard-patch-from-mozbz-1282015.patch \
            file://wayland-patches/0019-WIP-Added-build-config-when-wayland-is-not-enabled-o.patch \
            file://wayland-patches/0020-Added-enable-wayland-configure-option.patch \
            file://wayland-patches/0021-Use-MOZ_WAYLAND-instead-of-GDK_WINDOWING_WAYLAND.patch \
            file://wayland-patches/0022-Don-t-install-libmozwayland-when-wayland-is-disabled.patch \
            file://wayland-patches/0023-Improved-wayland-configure-defines.patch \
            file://wayland-patches/0024-Updated-configure-script-according-to-mozbz-1299083.patch \
            file://wayland-patches/0025-Removed-event-queue-from-mozcontainer.patch \
            file://wayland-patches/0026-WindowSurfaceWayland-refactorization.patch \
            file://wayland-patches/0027-tabs-replacement.patch \
            file://wayland-patches/0028-Optimized-back-buffer-buffer-switches.patch \
            file://wayland-patches/0029-Don-t-read-wayland-events-when-poll-fails.patch \
            file://wayland-patches/0030-Force-release-unused-back-buffers.patch \
            file://wayland-patches/0031-Moved-wayland-loop-to-Compositor-thread.patch \
            file://wayland-patches/0032-Removed-ImageBuffer-and-draw-directly-to-wayland-bac.patch \
            file://wayland-patches/0033-Removed-old-comments.patch \
            file://wayland-patches/0034-Fixed-crash-when-pasted-to-clipboard.patch \
            file://wayland-patches/0001-Disabled-broadway-backend-does-not-work-and-enabled-.patch \
            file://wayland-patches/0002-Added-D-Bus-remote-files.patch \
            file://wayland-patches/0003-replace.patch \
            file://wayland-patches/0004-WIP-DBus-remote-backend.patch \
            file://wayland-patches/0005-in-place-dbus.patch \
            file://wayland-patches/0006-tab-replacement.patch \
            file://wayland-patches/0007-tweaking.patch \
            file://wayland-patches/0008-Removed-unused-files.patch \
            file://wayland-patches/0001-Build-fix-for-nsGTKRemoteService.patch \
            file://wayland-patches/0001-Add-a-missing-CXXFLAGS-for-nsGTKRemoteService.patch \
            file://wayland-patches/0001-Fixed-dbus-params.patch \
            file://wayland-patches/0001-Add-a-missing-CXXFLAGS-for-XRemoteClient.patch \
            file://wayland-patches/0001-fixed-default-profile-name.patch \
            file://wayland-patches/0002-Better-wayland-shutdown.patch \
            file://wayland-patches/0003-Better-wayland-shutdown.patch \
            file://wayland-patches/0004-Fixed-hang-up-at-browser-quit.patch \
            file://wayland-patches/0005-Fixed-freeze-at-browser-quit-and-fixed-wayland-rende.patch \
            file://wayland-patches/0001-Reworked-multi-thread-rendering-code-provided-nsWayl.patch \
            file://wayland-patches/0002-Code-clean-up-distinguish-between-wl_display-and-nsW.patch \
            file://wayland-patches/0003-Fixed-wayland-surface-mapping-create-wayland-surface.patch \
            file://wayland-patches/0001-Call-wl_display_roundtrip-twice-to-ensure-we-have-va.patch \
            file://wayland-patches/0001-Use-wl_display_dispatch_queue_pending-to-fetch-wayla.patch \
            file://wayland-patches/0001-Fix-setting-up-shellHasCSD-flag-position.patch \
            file://wayland-patches/0001-Fixed-fullscreen-on-Weston.patch \
            file://wayland-patches/0001-Fixed-clipboard-crashes-after-browser-start-rhbz-145.patch \
            file://wayland-patches/0001-Fixed-error-handling-for-posix_fallocate-ftruncate-b.patch \
            file://wayland-patches/0002-Fixed-error-handling-for-posix_fallocate-and-formatt.patch \
            file://wayland-patches/0003-Fixed-rhbz-1464017-Wayland-Hamburger-menu-popup-and-.patch \
            file://wayland-patches/0001-Don-t-call-gdk_x11_window_get_xid-from-LOG-under-way.patch \
            file://wayland-patches/0002-Removed-the-gdk_seat_-code-let-s-solve-https-bugzill.patch \
            file://wayland-patches/0003-Don-t-explicitly-grab-on-Wayland-use-only-implicit-g.patch \
            file://wayland-patches/0001-Don-t-crash-when-we-re-missing-clipboard-data-rhbz-1.patch \
            file://wayland-patches/0001-Fixed-rendering-of-noautohide-panels-rhbz-1466377.patch \
            file://wayland-patches/0002-Use-subsurfaces-for-popup-creation-rhbz-1457201.patch \
            file://wayland-patches/0003-Fixed-mouse-transparency-for-popups-rhbz-1466377.patch \
            file://wayland-patches/0004-Added-missing-gtk_widget_input_shape_combine_region-.patch \
            file://wayland-patches/0006-Map-Wayland-subsurface-only-when-GdkWindow-is-alread.patch \
            file://wayland-patches/0007-Destroy-GdkWindow-owned-by-mozcontainer-when-unreali.patch \
            file://wayland-patches/0008-Remove-unrealize-handler-rhbz-1467104.patch \
            file://wayland-patches/0001-Tweaked-wl_surface_damage-calls.patch \
            file://wayland-patches/0002-Fixed-rhbz-1464916-missing-popup-rendering.patch \
           ', \
           '', d)}"

# Gecko Embedded's Additional wayland patches
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland', \
           ' \
            file://wayland-patches/0001-Permit-to-use-gtk-wayland-3.0-3.18.patch \
            file://wayland-patches/0001-Add-ad-hoc-solution-to-enable-Alt-modifier-on-Waylan.patch \
           ', \
           '', d)}"

# Gecko Embedded's Additional wayland patches to support EGL
#
# Current EGL patches doesn't work well on windowed mode.
# To avoid this issue, force use fullscreen mode.
# In addition, e10s (multi process window) isn't also supported yet.
SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'wayland egl', \
           'file://wayland-patches/0035-GLLibraryEGL-Use-wl_display-to-get-EGLDisplay-on-Way.patch \
            file://wayland-patches/0036-Use-wl_egl_window-as-a-native-EGL-window-on-Wayland.patch \
            file://wayland-patches/0037-Disable-query-EGL_EXTENSIONS.patch \
            file://wayland-patches/0038-Wayland-Detect-existence-of-wayland-libraries.patch \
            file://wayland-patches/0039-Wayland-Resize-wl_egl_window-when-the-nsWindow-is-re.patch \
            file://wayland-patches/0040-GLContextPrividerEGL-Remove-needless-code.patch \
            file://wayland-patches/0001-Enable-sharing-SharedSurface_EGLImage.patch \
            file://wayland-patches/0002-Add-workaround-for-eglDestroyImageKHR-SEGV.patch \
            file://wayland-patches/0001-Create-workaround-to-use-BasicCompositor-to-prevent-.patch \
            file://wayland-patches/0001-Call-fEGLImageTargetTexture2D-eariler.patch \
            file://wayland-patches/frameless.patch \
            file://e10s.js \
            file://wayland-patches/0001-Set-ui.popup.disable_autohide-as-true-to-enable-clic.patch \
           ', \
           '', d)}"

# Add a config file to enable GPU acceleration by default.
SRC_URI += "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', \
           'file://gpu.js', '', d)}"

SRC_URI += "${@bb.utils.contains('PACKAGECONFIG', 'openmax', \
           'file://openmax/0001-Add-initial-implementation-of-PureOmxPlatformLayer.patch \
            file://openmax/0002-OmxDecoderModule-Fix-a-bug-which-crashes-about-suppo.patch \
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
    install -m 0644 ${WORKDIR}/autoconfig.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
    install -m 0644 ${WORKDIR}/autoconfig.cfg ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'glx egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/gpu.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains('PACKAGECONFIG', 'openmax', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/openmax/openmax.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
    fi
    if [ -n "${@bb.utils.contains_any('PACKAGECONFIG', 'wayland egl', '1', '', d)}" ]; then
        install -m 0644 ${WORKDIR}/e10s.js ${D}${libdir}/${PN}-${MOZ_APP_BASE_VERSION}/defaults/pref/
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
