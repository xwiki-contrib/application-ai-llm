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
package org.xwiki.contrib.llm;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.xwiki.stability.Unstable;

/**
 * A chat message.
 *
 * @version $Id$
 * @since 0.3
 */
@Unstable
public class ChatMessage
{
    private final String role;

    private final String content;

    /**
     * Creates a new chat message.
     *
     * @param role the role of the message author, e.g., user, assistant, system or tool.
     * @param content the content of the message
     */
    public ChatMessage(String role, String content)
    {
        this.role = role;
        this.content = content;
    }

    /**
     * @return the role of the message author, e.g., user, assistant, system or tool.
     */
    public String getRole()
    {
        return role;
    }

    /**
     * @return the content of the message
     */
    public String getContent()
    {
        return content;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChatMessage that = (ChatMessage) o;

        return new EqualsBuilder().append(getRole(), that.getRole())
            .append(getContent(), that.getContent()).isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(getRole()).append(getContent()).toHashCode();
    }
}
