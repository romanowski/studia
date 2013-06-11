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

package pl.edu.agh.semantic.utils;

import com.hp.hpl.jena.shared.PrefixMapping;

import java.util.HashSet;
import java.util.Set;

/**
 * Various prefix-related utilities. Currently
 * for checking whether given prefix is well-known.
 * JENA well-known prefix list is used for the
 * implementation.
 *
 * @author Tomasz Zdybał
 */
public class PrefixUtil {
	private static Set<String> wellKnown = new HashSet<String>();

	static {
		wellKnown.addAll(PrefixMapping.Extended.getNsPrefixMap().values());
		wellKnown.add("http://xmlns.com/foaf/0.1/");
		wellKnown.add("http://www.w3.org/XML/1998/namespace");
	}

	/**
	 * Checks whether given prefix is well-known.
	 * 
	 * @param prefix ontology prefix
	 * @return true if well-known, false otherwise.
	 */
	public static boolean isWellKnown(String prefix) {
		return wellKnown.contains(prefix);
	}
}