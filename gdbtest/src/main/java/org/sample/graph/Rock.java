package org.sample.graph;

import xp.nova.sqldb.graph.DisableVersioning;
import xp.nova.sqldb.graph.NodeObject;

@DisableVersioning
public class Rock extends NodeObject
{
    public String name;
}
