/*******************************************************************************
 * Copyright (C) 2017-2019 Kat Fung Tjew
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.nova.sqlserver;

import java.nio.charset.Charset;
import java.util.HashMap;

import org.nova.frameworks.CoreEnvironment;
import org.nova.json.ObjectMapper;
import org.nova.logging.Item;
import org.nova.logging.Level;
import org.nova.logging.Logger;
import org.nova.metrics.SourceEventBoard;
import org.nova.sqldb.Accessor;
import org.nova.sqldb.Connector;
import org.nova.sqldb.Row;
import org.nova.sqldb.RowSet;
import org.nova.sqldb.SqlServerConnector;
import org.nova.tracing.Trace;
import org.nova.utils.FileUtils;

public class DatabaseUpdater
{
    static public final boolean existsTable(Trace parent,Connector connector,String tableName) throws Throwable
    {
        return connector.executeQuery(parent, null, "SELECT * FROM INFORMATION_SCHEMA.TABLES  WHERE TABLE_NAME=?",tableName).size()==1;
    }
    
    final private DatabaseUpdateActions blocked;
    final private DatabaseUpdateActions executed;
    final private DatabaseUpdatePermissions permissions;
    final private Logger logger;
    final private SourceEventBoard statusBoard;

    public DatabaseUpdater(CoreEnvironment coreEnvironment,DatabaseUpdatePermissions permissions)
    {
        this.logger=coreEnvironment.getLogger();
        this.statusBoard=coreEnvironment.getSourceEventBoard();
        if (permissions==null)
        {
            permissions=new DatabaseUpdatePermissions();
        }
        this.permissions=permissions;
        this.blocked=new DatabaseUpdateActions();
        this.executed=new DatabaseUpdateActions();
    }
    
    public Connector connectAndMigrate(Trace parent,CoreEnvironment coreEnvironment,ConnectorAndMigrationConfiguration configuration) throws Throwable
    {
        
        SqlServerConnector connector=new SqlServerConnector(coreEnvironment.getTraceManager(), coreEnvironment.getLogger(), configuration.user, coreEnvironment.getVault(), configuration.passwordKey, configuration.connectorConfiguration);
        migrate(parent,connector,configuration.scriptFile,Charset.forName(configuration.charSet));
        return connector;
    }
    
    public void migrate(Trace parent,Connector connector,String scriptFile,Charset charset) throws Throwable
    {
        this.logger.log(Level.NOTICE,"Start of database updates");
        try
        {
            if (scriptFile==null)
            {
                return;
            }
            String text=FileUtils.readTextFile(scriptFile,charset);
            SqlServerObjects objects=ScriptParser.parse(text);
            try (Accessor accessor=connector.openAccessor(parent))
            {
                this.logger.log(Level.NOTICE,"Table updates");
                for (Table table:objects.getTables())
                {
                    migrate(parent,table,accessor);
                }
                this.logger.log(Level.NOTICE,"Function updates");
                for (Function function:objects.getFunctions())
                {
                    migrate(parent,function,accessor);
                }
                this.logger.log(Level.NOTICE,"Procedure updates");
                for (Procedure procedure:objects.getProcedures())
                {
                    migrate(parent,procedure,accessor);
                }
            }
        }
        finally
        {
            String executed=ObjectMapper.writeObjectToString(this.executed);
            String blocked=ObjectMapper.writeObjectToString(this.blocked);
            this.logger.log(Level.NOTICE,"End of database updates",new Item("blocked",blocked),new Item("executed",executed));
            this.statusBoard.set("blocked@"+this.getClass().getName(),blocked);
            this.statusBoard.set("executed@"+this.getClass().getName(),executed);
        }
    }
    
  
    private void migrate(Trace parent,Table table,Accessor accessor) throws Throwable
    {
        String ownerName=table.getOwner()!=null?'['+table.getOwner()+"].["+table.getName()+']':'['+table.getName()+']';
        RowSet rowSet=accessor.executeQuery(parent, null,"SELECT sys.columns.name,sys.types.name as type,sys.columns.is_nullable,sys.columns.is_identity,sys.columns.max_length  FROM sys.columns JOIN sys.types ON sys.columns.system_type_id=sys.types.system_type_id WHERE object_id = OBJECT_ID(?) order by column_id",ownerName);
        if (rowSet.size()==0)
        {
            if (this.permissions.createTable==false)
            {
                this.blocked.createTable++;
            }
            accessor.executeUpdate(parent, null, table.getText());
            this.executed.createTable++;
        }
        else
        {
            HashMap<String,Column> map=new HashMap<>();
            for (Column column:table.getColumns())
            {
                map.put(column.getName(), column);
            }
            for (Row row:rowSet.rows())
            {
                String name=row.getVARCHAR("name");
                String type=row.getVARCHAR("type");
                Column column=map.get(name);
                if (column!=null)
                {
                    map.remove(name);
                }
            }
            
            boolean block=false;
            StringBuilder sb=new StringBuilder();
            if (map.size()>0)
            {
                sb.append("ADD");
                boolean commaNeeded=false;
                for (Column column:map.values())
                {
                    if (column.isNullAllowed())
                    {
                        if (this.permissions.createNullableTableColumn==false)
                        {
                            this.blocked.createNullableTableColumn++;
                            block=true;
                            continue;
                        }
                        this.executed.createNullableTableColumn++;
                    }
                    else
                    {
                        if (this.permissions.createNotNullableTableColumn==false)
                        {
                            this.blocked.createNotNullableTableColumn++;
                            block=true;
                            continue;
                        }
                        this.executed.createNotNullableTableColumn++;
                    }
                    if (column.isIdentity())
                    {
                      //TODO: not supported for now. To support, needs to detect if 
                        throw new Exception(); 
                    }
                    if (commaNeeded)
                    {
                        sb.append(',');
                    }
                    commaNeeded=true;
                    sb.append(" ["+column.getName()+"] "+column.getType());
                    if (column.getSize()!=null)
                    {
                        sb.append("("+column.getSize()+")");
                    }
                    if (column.isNullAllowed()==false)
                    {
                        throw new Exception("Table "+table.getName()+" cannot be forward migrated: New column not null. column="+column.getName()); 
                    }
                    sb.append(" NULL");
                }
            }
            if (block==false)
            {
                if (sb.length()>0)
                {
                    String alter="ALTER TABLE "+ownerName+" "+sb.toString();
                    try
                    {
                        accessor.executeUpdate(parent, null,alter);
                    }
                    catch (Throwable t)
                    {
                        throw new Exception("ALTER TABLE:"+alter,t);
                    }
                    this.logger.log(Level.NOTICE,"alter table",new Item("table",ownerName),new Item("alter",alter));
                }
            }
        }
    }

    private void migrate(Trace parent,Function function,Accessor accessor) throws Throwable
    {
        String ownerName=function.getOwner()!=null?'['+function.getOwner()+"].["+function.getName()+']':'['+function.getName()+']';
        RowSet rowSet=accessor.executeQuery(parent, null,"SELECT OBJECT_ID(?,'F')",ownerName);
        if ((rowSet.size()!=0)&&(rowSet.getRow(0).get(0)!=null))
        {
            if (this.permissions.replaceFunction==false)
            {
                this.blocked.replaceFunction++;
                return;
            }
            String drop="DROP FUNCTION "+ownerName;
            accessor.executeUpdate(parent, null, drop);
            this.executed.replaceFunction++;
            return;
        }
        else
        {
            accessor.executeUpdate(parent, null, function.getText());
            this.executed.createFunction++;
            return;
        }
    }
    
    private void migrate(Trace parent,Procedure procedure,Accessor accessor) throws Throwable
    {
        String ownerName=procedure.getOwner()!=null?'['+procedure.getOwner()+"].["+procedure.getName()+']':'['+procedure.getName()+']';
        try
        {
            RowSet rowSet=accessor.executeQuery(parent, null,"SELECT OBJECT_ID(?,'P')",ownerName);
            if ((rowSet.size()!=0)&&(rowSet.getRow(0).get(0)!=null))
            {
                if (this.permissions.replaceProcedure==false)
                {
                    this.blocked.replaceProcedure++;
                    return;
                }
                String drop="DROP PROCEDURE "+ownerName;
                accessor.executeUpdate(parent, null, drop);
                accessor.executeUpdate(parent, null, procedure.getText());
                this.executed.replaceProcedure++;
                return;
            }
            else
            {
                accessor.executeUpdate(parent, null, procedure.getText());
                this.executed.createProcedure++;
                return;
            }
        }
        catch (Throwable t)
        {
            throw new Exception("Migrate failed: "+ownerName,t);
        }
    }
    
}
