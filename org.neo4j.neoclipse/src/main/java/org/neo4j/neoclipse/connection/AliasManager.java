/**
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.neo4j.neoclipse.connection;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.neo4j.neoclipse.Activator;
import org.neo4j.neoclipse.ApplicationUtils;
import org.neo4j.neoclipse.XMLUtils;
import org.neo4j.neoclipse.graphdb.GraphDbServiceManager;

/**
 * Maintains the list of Neo4JConnection Alias
 * 
 * @author Radhakrishna Kalyan
 * 
 */
public class AliasManager implements ConnectionListener
{

    private final Set<Alias> aliases = new HashSet<Alias>();
    private final List<ConnectionListener> connectionListeners = new LinkedList<ConnectionListener>();

    public void loadAliases()
    {
        aliases.clear();

        Element root = XMLUtils.readRoot( new File( ApplicationUtils.USER_ALIAS_FILE_NAME ) );
        if ( root != null )
        {
            List<Element> elements = root.elements( Alias.ALIAS );
            if ( root.getName().equals( Alias.ALIASES ) )
            {
                for ( Element aliasElement : elements )
                {
                    addAlias( new Alias( aliasElement ) );
                }
            }

        }
    }

    /**
     * Saves all the Aliases to the users preferences
     * 
     */
    public void saveAliases()
    {
        DefaultElement root = new DefaultElement( Alias.ALIASES );
        for ( Alias alias : aliases )
        {
            root.add( alias.describeAsXml() );
        }

        XMLUtils.save( root, new File( ApplicationUtils.USER_ALIAS_FILE_NAME ) );
    }

    public void addAlias( Alias alias )
    {
        aliases.add( alias );
        modelChanged();
    }

    public void removeAlias( Alias alias )
    {
        GraphDbServiceManager graphDbServiceManager = Activator.getDefault().getGraphDbServiceManager();
        if ( graphDbServiceManager.isRunning() && graphDbServiceManager.getCurrentAlias().equals( alias ) )
        {
            MessageDialog.openWarning( Display.getCurrent().getActiveShell(), "Delete Connection",
                    "Please stop the service before deleting." );
            return;
        }

        aliases.remove( alias );
        modelChanged();
    }

    public Collection<Alias> getAliases()
    {
        return aliases;
    }

    public void registerConnetionListener( ConnectionListener listener )
    {
        connectionListeners.add( listener );
    }

    /**
     * Called to notify that the list of connections has changed; passes this
     * onto the listeners
     */
    @Override
    public void modelChanged()
    {
        for ( ConnectionListener listener : connectionListeners )
        {
            listener.modelChanged();
        }
    }

}
