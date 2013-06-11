/*
 * Copyright (c) 2011,2012, Krzysztof Styrc and Tomasz Zdybał
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE PROJECT OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package pl.edu.agh.semantic.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.semantic.enums.DataSourceType;

/**
 * Holds information about indexed data source.
 *
 * @author Tomasz Zdybał
 */
public class IndexEntry {
	
	private static final Logger log = LoggerFactory.getLogger(IndexEntry.class);
	
	/**
	 * Type of data source.
	 */
	private DataSourceType type;

	/**
	 * URL of data source.
	 */
	private String url;

	private boolean enabled;
	
	/**
	 * Default constructor for index entry.
	 * Creates object with unset fields.
	 */
	public IndexEntry() {
		this.enabled = true;
	}

	/**
	 * Creates index entry with specified type and url.
	 *
	 * @param type type of data source in index entry
	 * @param url  url of data source
	 */
	public IndexEntry(DataSourceType type, String url) {
		this.type = type;
		this.url = url;
		this.enabled = true;
	}

	public DataSourceType getType() {
		return type;
	}

	public void setType(DataSourceType type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		log.info("setEnabled: " + enabled + " : " + url);
		this.enabled = enabled;
	}
	
	/**
	 * @inheritDoc
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		IndexEntry that = (IndexEntry) o;

		return type == that.type && url.equals(that.url);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + url.hashCode();
		return result;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("IndexEntry");
		sb.append("{type=").append(type);
		sb.append(", url='").append(url).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
