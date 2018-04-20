<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <head>
                <title>Paper</title>
            </head>
            <body style="font-family: Times New Roman; margin-left: 50px; margin-right: 50px;">
                <h1 style="text-align: center; font-weight: bold;">
                    <xsl:value-of select="paper/title"/>
                </h1>
                <xsl:apply-templates select="paper"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="paper">
        <xsl:apply-templates select="head"/>
        <xsl:apply-templates select="authors"/>
        <xsl:apply-templates select="abstract"/>
        <xsl:apply-templates select="section"/>
        <xsl:apply-templates select="bibliography"/>
    </xsl:template>

    <xsl:template match="paper/authors">
        <p style="text-align: center; font-weight: normal;">
            <xsl:for-each select="author">
                <a>
                    <xsl:attribute name="href">
                        http://localhost:4200/authors/<xsl:value-of select="@username"/>/papers
                    </xsl:attribute>
                    <xsl:value-of select="name"/>
                </a>
                <br/>
                <i>
                    <xsl:value-of select="institution/name"/> &#xA0;<xsl:value-of select="institution/address"/>
                </i>
                <br/>
            </xsl:for-each>
        </p>
        <br/>
        <br/>
    </xsl:template>

    <xsl:template match="paper/abstract">

        <b style="font-size: 20px;">Abstract</b>
        <p>
            <b>Purpose&#xA0;&#xA0;</b>
            <xsl:value-of select="purpose"/>
            <br/>
            <b>Design/Methodology/Approach&#xA0;&#xA0;</b>
            <xsl:value-of select="designMethodologyApproach"/>
            <br/>
            <b>Findings&#xA0;&#xA0;</b>
            <xsl:value-of select="findings"/>
            <br/>
            <b>Research Limitations Implications&#xA0;&#xA0;</b>
            <xsl:value-of select="researchLimitationsImplications"/>
            <br/>
            <b>Practical Implications&#xA0;&#xA0;</b>
            <xsl:value-of select="practicalImplications"/>
            <br/>
            <b>Originality Value&#xA0;&#xA0;</b>
            <xsl:value-of select="originalityValue"/>
            <br/>
            <b>Keywords&#xA0;&#xA0;</b>
            <xsl:for-each select="keywords/keyword">
                <xsl:if test="position()>1">,
                </xsl:if>
                <xsl:value-of select="."/>
            </xsl:for-each>
            <br/>
            <b>Paper Type&#xA0;&#xA0;</b>
            <xsl:value-of select="paperType"/>
            <br/>
        </p>
    </xsl:template>

    <xsl:template match="paper/section">
        <xsl:call-template name="contentTemplate">
            <xsl:with-param name="counter" select="30"/>
        </xsl:call-template>
        <br/>
        <xsl:if test="section">
            <xsl:apply-templates select="section">
                <xsl:with-param name="count" select="26"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <xsl:template match="section">
        <xsl:param name="count"/>
        <xsl:call-template name="contentTemplate">
            <xsl:with-param name="counter" select="$count"/>
        </xsl:call-template>
        <br/>
        <xsl:if test="section">
            <xsl:apply-templates select="section">
                <xsl:with-param name="count" select="$count*0.8"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <xsl:template name="contentTemplate">
        <xsl:param name="counter"/>
        <b style="font-size: {$counter}px;">
            <xsl:value-of select="heading"/>
        </b>
        <p>
            <xsl:apply-templates select="content"/>
        </p>
    </xsl:template>

    <xsl:template match="content">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="content//text()">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="content//reference">
        <a>
            <xsl:attribute name="href">
                #<xsl:value-of select="@refId"/>
            </xsl:attribute>
            [<xsl:value-of select="@refId"/>]
        </a>
    </xsl:template>

    <xsl:template match="content//image">
        <figure style='text-align:center; display:block;'>
            <img style='max-height: 500px; max-width: 500px;'>
                <xsl:attribute name="src">
                    <xsl:value-of select="@link"/>
                </xsl:attribute>
                <xsl:attribute name="alt">
                    <xsl:value-of select="self::node()"/>
                </xsl:attribute>
            </img>
            <figcaption style="font-family: Times New Roman;">
                <xsl:value-of select="self::node()"/>
            </figcaption>
        </figure>
    </xsl:template>

    <xsl:template match="bibliography">
        <b style="font-size: 30px;">Bibliography</b>
        <ul style="list-style: none; padding: 0; margin: 0;">
            <xsl:for-each select="reference">
                <li>
                    <xsl:attribute name="id">
                        <xsl:value-of select="@id"/>
                    </xsl:attribute>
                    <a>
                        <xsl:attribute name="href">
                            http://localhost:4200/papers/<xsl:value-of select="@paperId"/>
                        </xsl:attribute>
                        [<xsl:value-of select="@id"/>]
                        <xsl:value-of select="text()"/>
                    </a>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="head">
        <br/>
        <br/>
        <i>Revision
            <xsl:value-of select="revisionNumber"/>
        </i>
        <br/>
        <i>Published on
            <xsl:value-of select="publishDate"/>
        </i>
    </xsl:template>

</xsl:stylesheet>