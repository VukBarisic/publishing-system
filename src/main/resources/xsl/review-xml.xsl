<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <review xmlns:review="http://ftn.uns.ac.rs/code10/review"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://ftn.uns.ac.rs/code10/review file:/E:/dev/ij_ws/publishing-system/src/main/resources/xsd/review.xsd">
        <xsl:apply-templates select="//comment"/>
        </review>
    </xsl:template>

    <xsl:template match="//comment">
        <xsl:copy-of select="self::node()"/>
    </xsl:template>


</xsl:stylesheet>