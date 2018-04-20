<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <head>
                <title>Evaluation form</title>
            </head>
            <body style="font-family: Times New Roman; margin-left: 50px; margin-right: 50px;">
                <xsl:apply-templates select="evaluationForm"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="evaluationForm">
        <p>
            <b>Paper originality:&#xA0;</b>
            <xsl:value-of select="originality/@grade"/>
            <br/>
            <b>Abstract, keywords, content adequacy:&#xA0;</b>
            <xsl:value-of select="keywords/@grade"/>
            <br/>
            <b>Agreement with current knowledge in the field:&#xA0;</b>
            <xsl:value-of select="previousWork/@grade"/>
            <br/>
            <b>Conclusions correspondence to the results:&#xA0;</b>
            <xsl:value-of select="conclusions/@grade"/>
            <br/>
            <b>Quality of experiments:&#xA0;</b>
            <xsl:value-of select="experiments/@grade"/>
            <br/>
            <b>Paper layout:&#xA0;</b>
            <xsl:value-of select="layout/@grade"/>
            <br/>
            <b>Language style:&#xA0;</b>
            <xsl:value-of select="languageStyle/@grade"/>
            <br/>
            <b>Practical and innovative value:&#xA0;</b>
            <xsl:value-of select="value/@grade"/>
            <br/>
            <b>Figures, graphs and tables quality and conformity to current knowledge in the field:&#xA0;</b>
            <xsl:value-of select="figures/@grade"/>
            <br/>
            <b>Correct reference citations:&#xA0;</b>
            <xsl:value-of select="references/@grade"/>
            <br/>
            <br/>
            <b>The paper can be submitted for publication:&#xA0;</b>
            <xsl:value-of select="canBePublished/@grade"/>
            <br/>
        </p>

        <p>
            <b>Justification for the evaluation of the paper:&#xA0;</b>
            <xsl:value-of select="justification"/>
        </p>
    </xsl:template>

</xsl:stylesheet>