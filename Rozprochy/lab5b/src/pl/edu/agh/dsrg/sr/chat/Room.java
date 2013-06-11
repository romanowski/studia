package pl.edu.agh.dsrg.sr.chat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgroups.JChannel;

public class Room {

	Set<String> users = Collections.synchronizedSet(new HashSet<String>());

	boolean isOpen = false;

	JChannel channel;

}
