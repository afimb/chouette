/**
 * Copyright (c) 2012 scireum GmbH - Andreas Haufler - aha@scireum.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package fr.certu.chouette.exchange.netex.xml;

import java.util.List;

import javax.xml.xpath.XPathExpressionException;

/**
 * Represents a structured node, which is part of a {@link StructuredInput}.
 * 
 * @author aha
 * 
 */
public interface StructuredNode {

    /**
     * Returns a given node at the relative path.
     */
    StructuredNode queryNode(String xpath) throws XPathExpressionException;

    /**
     * Returns a list of nodes at the relative path.
     */
    List<StructuredNode> queryNodeList(String xpath)
            throws XPathExpressionException;

    /**
     * Boilerplate for array handling....
     */
    StructuredNode[] queryNodes(String path) throws XPathExpressionException;

    /**
     * Returns a property at the given part.
     */
    String queryString(String path) throws XPathExpressionException;

    /**
     * Queries a {@link Value} which provides various conversions.
     */
    Value queryValue(String path) throws XPathExpressionException;

    /**
     * Checks whether a node or non-empty content is reachable via the given
     * XPath.
     */
    boolean isEmpty(String path) throws XPathExpressionException;

    /**
     * Returns the current node's name.
     */
    String getNodeName();
}
