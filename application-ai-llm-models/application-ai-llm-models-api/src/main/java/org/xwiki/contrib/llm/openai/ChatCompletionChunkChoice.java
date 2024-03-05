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
package org.xwiki.contrib.llm.openai;

import org.xwiki.stability.Unstable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.theokanning.openai.completion.chat.ChatMessage;

/**
 * Represents a choice in a chat completion chunk.
 *
 * @param index the index of the choice
 * @param delta the delta message
 * @param finishReason the reason the chat completion finished
 *
 * @version $Id$
 * @since 0.3
 */
@Unstable
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChatCompletionChunkChoice(
    Integer index,
    ChatMessage delta,
    String finishReason)
{
}
