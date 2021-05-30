include firefox_78.10.1esr.bb

FILESEXTRAPATHS_prepend := "${THISDIR}/firefox:"

SRC_URI_append = "file://amethyst/disable-addon-autoupdate.patch \
          	  file://amethyst/disable-attribution-code.patch \
         	  file://amethyst/disable-captivedetect.patch \
         	  file://amethyst/disable-crashreporter.patch \
         	  file://amethyst/disable-fxaccounts.patch \
         	  file://amethyst/disable-homepage-override.patch \
         	  file://amethyst/disable-ocsp.patch \
         	  file://amethyst/disable-remote-settings.patch \
         	  file://amethyst/disable-safebrowsing.patch \
         	  file://amethyst/disable-searchengine-update.patch \
         	  file://amethyst/disable-snippets.patch \
         	  file://amethyst/disable-telemetry.patch \
         	  file://amethyst/disable-updater.patch \
         	  file://amethyst/disable-webpush.patch \
         	  file://amethyst/disable-wifigeo.patch \
         	  file://amethyst/enable-form-validation.patch \
         	  file://amethyst/enable-webrtc.patch \
         	  file://amethyst/branding.patch \
         	  file://amethyst/webviewer.patch \
         	  file://amethyst/browser/amethyst/branding \
                 "
TOOLCHAIN_pn-webviewer = "clang"
AS_pn-webviewer = "${CC}"

do_configure_prepend() {
cp -r ../amethyst/browser/amethyst ./browser
echo "ac_add_options --with-branding=browser/amethyst/branding" >> ../mozconfig
}
