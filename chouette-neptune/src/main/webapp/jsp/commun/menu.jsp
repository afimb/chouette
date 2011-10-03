<%@ taglib uri="/struts-tags" prefix="s"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<br />
<%--Neptune validation --%>
<a href="<%=request.getContextPath()%>/neptune-validation/execute.action" style="font-size:24px;">
    <b><s:text name="text.neptune.import"/>(&#x2646;)</b>
<br/><br/>
<div id="menu">
    <div class="submenu">
        <div><s:text name="text.menu"/></div>
        <ul id="sommaire">
            <%-- Reseaux --%>
            <s:url id="urlReseauxMenu" action="list" namespace="/network" includeParams="none"/>
            <li><s:a href="%{urlReseauxMenu}" id="reseauxMenu"><s:text name="text.reseaux"/></s:a></li>

            <%-- Transporteurs --%>
            <s:url id="transporteursMenu" action="list" namespace="/company" includeParams="none"/>
            <li><s:a href="%{transporteursMenu}" id="transporteursMenu"><s:text name="text.transporteurs"/></s:a></li>

            <%-- Lignes --%>
            <s:url id="urlLignesMenu" action="list" namespace="/line" includeParams="none"/>
            <li><s:a href="%{urlLignesMenu}" id="lignesMenu"><s:text name="text.lignes"/></s:a></li>

            <%-- Itineraires --%>
            <li>
                <ul id="sub_sommaire">
                    <s:url id="urlItinerairesMenu" action="search" namespace="/route" includeParams="none"/>
                    <li><s:a href="%{urlItinerairesMenu}" id="itinerairesMenu"><s:text name="text.itineraires"/></s:a></li>

                    <%-- Horaires --%>
                    <li>
                        <ul id="sub_sub_sommaire">
                            <s:url id="urlHorairesMenu" action="search" namespace="/vehicleJourneyAtStop" includeParams="none"/>
                            <li><s:a href="%{urlHorairesMenu}" id="horairesMenu"><s:text name="text.horaires"/></s:a></li>
                            </ul>
                        <li>
                    </ul>
                </li>

            <%-- Calendriers d'application --%>
            <s:url id="urlTableauMarchesMenu" action="list" namespace="/timeTable" includeParams="none"/>
            <li><s:a href="%{urlTableauMarchesMenu}" id="tableauMarchesMenu"><s:text name="text.tableauMarches"/></s:a></li>

            <%-- Arrets physiques --%>
            <s:url id="urlArretPhysiquesMenu" action="list" namespace="/boardingPosition" includeParams="none"/>
            <li><s:a href="%{urlArretPhysiquesMenu}" id="arretPhysiquesMenu"><s:text name="text.arretPhysiques"/></s:a></li>

            <%-- Zones --%>			
            <s:url id="urlZonesMenu" action="list" namespace="/stopPlace" includeParams="none" />
            <li><s:a href="%{urlZonesMenu}" id="zonesMenu"><s:text name="text.zones"/></s:a></li>

            <%-- Correspondances --%>	
            <s:url id="urlCorrespondancesMenu" action="list" namespace="/connectionLink" includeParams="none"/>
            <li><s:a href="%{urlCorrespondancesMenu}" id="correspondancesMenu"><s:text name="text.correspondances"/></s:a></li>

            <%-- ITL --%>	
            <s:url id="urlITLMenu" action="list" namespace="/routingConstraint" includeParams="none"/>
            <li><s:a href="%{urlITLMenu}" id="ITLMenu"><s:text name="text.ITL"/></s:a></li>
            
            
            <%-- Import --%>	
            <s:url id="urlImportMenu" action="execute" namespace="/upload" includeParams="none"/>
            <li><s:a href="%{urlImportMenu}" id="importMenu"><s:text name="text.import"/></s:a></li>


            <%-- Validation --%>	
            <s:url id="urlValidationMenu" action="Validation_execute" namespace="/" includeParams="none"/>
            <li><s:a href="%{urlValidationMenu}" id="validationMenu"><s:text name="text.validation"/></s:a></li>

        </ul>
    </div>
</div>
<%-- Test pour savoir quel va tre celui slectionn dans le menu --%>
<%-- Reseaux --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.reseau.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.reseau.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.reseau.update.title')">
    <SCRIPT type="text/javascript">
        $('reseauxMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('reseauxMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Transporteurs --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.transporteur.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.transporteur.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.transporteur.update.title')">
    <SCRIPT type="text/javascript">
        $('transporteursMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('transporteursMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Lignes --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.ligne.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.ligne.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.ligne.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.course.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.course.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.mission.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.mission.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.arretSurItineraire.list.title')">
    <SCRIPT type="text/javascript">
        $('lignesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('lignesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Itineraires --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.itineraire.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.itineraire.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.itineraire.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.itineraire.recherche.title')">
    <SCRIPT type="text/javascript">
        $('itinerairesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('itinerairesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Horaires --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.horairesDePassage.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.horairesDePassage.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.horairesDePassage.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.horairesDePassage.recherche.title')">
    <SCRIPT type="text/javascript">
        $('horairesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('horairesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Calendriers d'application --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.tableauMarche.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.tableauMarche.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.tableauMarche.update.title')">
    <SCRIPT type="text/javascript">
        $('tableauMarchesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('tableauMarchesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Arrets physiques --%>
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.arretPhysique.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.arretPhysique.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.arretPhysique.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.boardingPosition.search.title')">
    <SCRIPT type="text/javascript">
        $('arretPhysiquesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('arretPhysiquesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Zones --%>			
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.zone.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.zone.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.zone.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.stopPlace.search.title')">
    <SCRIPT type="text/javascript">
        $('zonesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('zonesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Correspondances --%>	
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.connectionlink.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.connectionlink.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.connectionlink.update.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.connectionlink.search.title')">
    <SCRIPT type="text/javascript">
        $('correspondancesMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('correspondancesMenu').className='';
    </SCRIPT>	
</s:else>

<%-- ITL --%>	
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.routingConstraint.list.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.routingConstraint.create.title')
      || filAriane.cleTexteDernierElementFilAriane == getText('text.routingConstraint.update.title')">
    <SCRIPT type="text/javascript">
        $('ITLMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('ITLMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Import --%>	
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('import.index.title')">
    <SCRIPT type="text/javascript">
        $('importMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('importMenu').className='';
    </SCRIPT>	
</s:else>

<%-- Validation --%>	
<s:if test="filAriane.cleTexteDernierElementFilAriane == getText('text.validation.list.title')">
    <SCRIPT type="text/javascript">
        $('validationMenu').className='selectionne';
    </SCRIPT>	
</s:if>
<s:else>
    <SCRIPT type="text/javascript">
        $('validationMenu').className='';
    </SCRIPT>	
</s:else>

