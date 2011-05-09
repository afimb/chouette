package fr.certu.chouette.service.amivif.base;

public class GroupOfLineConverter {

    public chouette.schema.GroupOfLine atc(amivif.schema.GroupOfLine amivifGroupOfLine) {
        if (amivifGroupOfLine == null) {
            return null;
        }
        chouette.schema.GroupOfLine chouetteGroupOfLine = new chouette.schema.GroupOfLine();
        chouetteGroupOfLine.setComment(amivifGroupOfLine.getComment());
        chouetteGroupOfLine.setCreationTime(amivifGroupOfLine.getCreationTime());
        chouetteGroupOfLine.setCreatorId(amivifGroupOfLine.getCreatorId());
        chouetteGroupOfLine.setLineId(amivifGroupOfLine.getLineId());
        chouetteGroupOfLine.setName(amivifGroupOfLine.getName());
        chouetteGroupOfLine.setObjectId(amivifGroupOfLine.getObjectId());
        if (amivifGroupOfLine.hasObjectVersion() && amivifGroupOfLine.getObjectVersion() >= 1) {
            chouetteGroupOfLine.setObjectVersion(amivifGroupOfLine.getObjectVersion());
        } else {
            chouetteGroupOfLine.setObjectVersion(1);
        }
        return chouetteGroupOfLine;
    }

    public chouette.schema.GroupOfLine[] atc(amivif.schema.GroupOfLine[] amivifGroupOfLines) {
        if (amivifGroupOfLines == null) {
            return new chouette.schema.GroupOfLine[0];
        }
        int totalGroupOflines = amivifGroupOfLines.length;
        chouette.schema.GroupOfLine[] chouetteGroupOflines = new chouette.schema.GroupOfLine[totalGroupOflines];
        for (int i = 0; i < totalGroupOflines; i++) {
            chouetteGroupOflines[i] = atc(amivifGroupOfLines[i]);
        }
        return chouetteGroupOflines;
    }

    public amivif.schema.GroupOfLine cta(chouette.schema.GroupOfLine chouetteGroupOfLine) {
        if (chouetteGroupOfLine == null) {
            return null;
        }
        amivif.schema.GroupOfLine amivifGroupOfLine = new amivif.schema.GroupOfLine();
        amivifGroupOfLine.setComment(chouetteGroupOfLine.getComment());
        amivifGroupOfLine.setCreationTime(chouetteGroupOfLine.getCreationTime());
        amivifGroupOfLine.setCreatorId(chouetteGroupOfLine.getCreatorId());
        amivifGroupOfLine.setLineId(chouetteGroupOfLine.getLineId());
        amivifGroupOfLine.setName(chouetteGroupOfLine.getName());
        amivifGroupOfLine.setObjectId(chouetteGroupOfLine.getObjectId());
        if (chouetteGroupOfLine.hasObjectVersion() && chouetteGroupOfLine.getObjectVersion() >= 1) {
            amivifGroupOfLine.setObjectVersion(chouetteGroupOfLine.getObjectVersion());
        } else {
            amivifGroupOfLine.setObjectVersion(1);
        }
        return amivifGroupOfLine;
    }

    public amivif.schema.GroupOfLine[] cta(chouette.schema.GroupOfLine[] chouetteGroupOfLines) {
        if (chouetteGroupOfLines == null) {
            return new amivif.schema.GroupOfLine[0];
        }
        int totalGroupOflines = chouetteGroupOfLines.length;
        amivif.schema.GroupOfLine[] amivifGroupOflines = new amivif.schema.GroupOfLine[totalGroupOflines];
        for (int i = 0; i < totalGroupOflines; i++) {
            amivifGroupOflines[i] = cta(chouetteGroupOfLines[i]);
        }
        return amivifGroupOflines;
    }
}
