/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.llm.rest;

import java.util.List;

import org.xwiki.contrib.llm.Collection;
import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Collection representation for the REST API.
 *
 * @version $Id$
 * @since 0.3
 */
@Unstable
public class JSONCollection
{
    private String name;


    @JsonProperty("embedding_model")
    private String embeddingModel;
    private String chunkingMethod;
    private int chunkingMaxSize;
    private int chunkingOverlapOffset;
    private List<String> documentSpaces;
    private String queryGroups;
    private String editGroups;
    private String adminGroups;
    private String rightsCheckMethod;
    private String rightsCheckMethodParam;

    /**
     * Construct a collection from a {@link Collection}.
     *
     * @param collection the collection to construct from
     */
    public JSONCollection(Collection collection)
    {
        this.name = collection.getName();
        this.embeddingModel = collection.getEmbeddingModel();
        this.chunkingMethod = collection.getChunkingMethod();
        this.chunkingMaxSize = collection.getChunkingMaxSize();
        this.chunkingOverlapOffset = collection.getChunkingOverlapOffset();
        this.documentSpaces = collection.getDocumentSpaces();
        this.queryGroups = collection.getQueryGroups();
        this.editGroups = collection.getEditGroups();
        this.adminGroups = collection.getAdminGroups();
        this.rightsCheckMethod = collection.getRightsCheckMethod();
        this.rightsCheckMethodParam = collection.getRightsCheckMethodParam();
    }

    /**
     * Applies the non-null properties of this collection to a {@link Collection}.
     *
     * @param collection the collection to apply to
     */
    public void applyTo(Collection collection)
    {
        if (this.embeddingModel != null) {
            collection.setEmbeddingModel(this.embeddingModel);
        }
        if (this.chunkingMethod != null) {
            collection.setChunkingMethod(this.chunkingMethod);
        }
        if (this.chunkingMaxSize != 0) {
            collection.setChunkingMaxSize(this.chunkingMaxSize);
        }
        if (this.chunkingOverlapOffset != 0) {
            collection.setChunkingOverlapOffset(this.chunkingOverlapOffset);
        }
        if (this.documentSpaces != null) {
            collection.setDocumentSpaces(this.documentSpaces);
        }
        if (this.queryGroups != null) {
            collection.setQueryGroups(this.queryGroups);
        }
        if (this.editGroups   != null) {
            collection.setEditGroups(this.editGroups);
        }
        if (this.adminGroups  != null) {
            collection.setAdminGroups(this.adminGroups);
        }
        if (this.rightsCheckMethod != null) {
            collection.setRightsCheckMethod(this.rightsCheckMethod);
        }
        if (this.rightsCheckMethodParam != null) {
            collection.setRightsCheckMethodParam(this.rightsCheckMethodParam);
        }
        collection.save();
    }

    /**
     * @return the name of the collection
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return the embedding model that should be used to embed chunks of the documents in this collection
     */
    public String getEmbeddingModel()
    {
        return this.embeddingModel;
    }

    /**
     * @return the chunking method that should be used to chunk the documents in this collection
     */
    public String getChunkingMethod()
    {
        return this.chunkingMethod;
    }

    /**
     * @return the maximum size of a chunk
     */
    public int getChunkingMaxSize()
    {
        return this.chunkingMaxSize;
    }

    /**
     * @return the overlap offset of chunks
     */
    public int getChunkingOverlapOffset()
    {
        return this.chunkingOverlapOffset;
    }

    /**
     * @return the list of spaces for the documents in the collection
     */
    public List<String> getDocumentSpaces()
    {
        return this.documentSpaces;
    }

    /**
     * @return the list of groups that can query the collection
     */
    public String getQueryGroups()
    {
        return this.queryGroups;
    }

    /**
     * @return the list of groups that can edit the collection's contents
     */
    public String getEditGroups()
    {
        return this.editGroups;
    }

    /**
     * @return the list of groups that can edit the collection's settings
     */
    public String getAdminGroups()
    {
        return this.adminGroups;
    }

    /**
     * @return the rights check method that should be used to check if a user has the right to query the collection
     */
    public String getRightsCheckMethod()
    {
        return this.rightsCheckMethod;
    }

    /**
     * @return optional parameter used by the rights check method
     */
    public String getRightsCheckMethodParam()
    {
        return this.rightsCheckMethodParam;
    }

    /**
     * @param name the name of the collection
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @param embeddingModel the embedding model that should be used to embed chunks of the documents in this collection
     */
    public void setEmbeddingModel(String embeddingModel)
    {
        this.embeddingModel = embeddingModel;
    }

    /**
     * @param chunkingMethod the chunking method that should be used to chunk the documents in this collection
     */
    public void setChunkingMethod(String chunkingMethod)
    {
        this.chunkingMethod = chunkingMethod;
    }

    /**
     * @param chunkingMaxSize the maximum size of a chunk
     */
    public void setChunkingMaxSize(int chunkingMaxSize)
    {
        this.chunkingMaxSize = chunkingMaxSize;
    }

    /**
     * @param chunkingOverlapOffset the overlap offset chunks
     */
    public void setChunkingOverlapOffset(int chunkingOverlapOffset)
    {
        this.chunkingOverlapOffset = chunkingOverlapOffset;
    }

    /**
     * @param documentSpaces the list of spaces for the documents in the collection
     */
    public void setDocumentSpaces(List<String> documentSpaces)
    {
        this.documentSpaces = documentSpaces;
    }

    /**
     * @param queryGroups the list of groups that can query the collection
     */
    public void setQueryGroups(String queryGroups)
    {
        this.queryGroups = queryGroups;
    }

    /**
     * @param editGroups the list of groups that can edit the collection
     */
    public void setEditGroups(String editGroups)
    {
        this.editGroups = editGroups;
    }

    /**
     * @param adminGroups the list of groups that can edit the collection settings
     */
    public void setAdminGroups(String adminGroups)
    {
        this.adminGroups = adminGroups;
    }
}
