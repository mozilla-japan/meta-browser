SUMMARY = "An old version of Autoconf that produce shell scripts to automatically configure software"
DESCRIPTION = "Autoconf2.13 is an old version of Autoconf which is an extensible package of M4 macros \
that produce shell scripts to automatically configure software source code packages. This version of \
Autoconf is required to build Firefox."
LICENSE = "GPLv2"
HOMEPAGE = "http://www.gnu.org/software/autoconf/"
SECTION = "devel"
DEPENDS += "m4-native"
DEPENDS_class-native = "m4-native"
DEPENDS_class-nativesdk = "nativesdk-m4"
RDEPENDS_${PN} = "m4 \
		  perl \
		  perl-module-file-find \
		 "
RDEPENDS_${PN}_class-native = "m4-native"
RDEPENDS_${PN}_class-nativesdk = "\
		  nativesdk-m4 \
		  nativesdk-perl \
		  nativesdk-perl-module-file-find \
                  "

SRC_URI = "${GNU_MIRROR}/autoconf/autoconf-${PV}.tar.gz \
           file://avoid-find.pl.patch \
           file://config-update.patch \
           file://other-debian.patch \
          "

inherit autotools

EXTRA_OECONF = "--program-suffix=2.13"
S = "${WORKDIR}/autoconf-${PV}"

PERL = "${USRBINPATH}/perl"
PERL_class-native = "/usr/bin/env perl"
PERL_class-nativesdk = "/usr/bin/env perl"

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='${PERL}'"

LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"

SRC_URI[md5sum] = "9de56d4a161a723228220b0f425dc711"
SRC_URI[sha256sum] = "f0611136bee505811e9ca11ca7ac188ef5323a8e2ef19cffd3edb3cf08fd791e"

BBCLASSEXTEND = "native nativesdk"

do_configure() {
	oe_runconf
}

do_install() {
        oe_runmake prefix=${D}${prefix} bindir=${D}${bindir} datadir=${D}${datadir} infodir=${D}${datadir}/info install
}
