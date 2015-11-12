/**
 * This file is hereby placed into the Public Domain. This means anyone is
 * free to do whatever they wish with this file.
 */
package mil.nga.giat.data.elasticsearch;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import java.awt.RenderingHints.Key;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * Data store factory that creates {@linkplain ElasticDataStore} instances.
 *
 */
public class ElasticDataStoreFactory implements DataStoreFactorySpi {

    /** Cluster hostname. **/
    public static final Param HOSTNAME = new Param("elasticsearch_host", String.class, "Elasticsearch host", false, "localhost");

    /** Cluster transport client port. **/
    public static final Param HOSTPORT = new Param("elasticsearch_port", Integer.class, "Elasticsearch port", false, 9300);

    /** Index name. **/
    public static final Param INDEX_NAME = new Param("index_name", String.class, "Index defining type", true);

    /** Index name. **/
    public static final Param SEARCH_INDICES = new Param("search_indices", String.class, "Indices for search (default is index_name)", false);

    /** Cluster name. **/
    public static final Param CLUSTERNAME = new Param("cluster_name", String.class, "Name of cluster", false, "elasticsearch");

    public static final Param LOCAL_NODE = new Param("use_local_node", Boolean.class, "Use node client", false, false);

    public static final Param STORE_DATA = new Param("store_data", Boolean.class, "Store data in local node", false, false);

    public static final Param DATA_PATH = new Param("data_path", String.class, "Data path (for testing)", false);
    
    @Override
    public String getDisplayName() {
        return "Elasticsearch";
    }

    @Override
    public String getDescription() {
        return "Elasticsearch Index";
    }

    @Override
    public Param[] getParametersInfo() {
        return new Param[]{HOSTNAME, HOSTPORT, INDEX_NAME, SEARCH_INDICES, CLUSTERNAME, LOCAL_NODE, STORE_DATA};
    }

    @Override
    public boolean canProcess(Map<String, Serializable> params) {
        try {
            final String searchHost = (String) HOSTNAME.lookUp(params);
            final String indexName = (String) INDEX_NAME.lookUp(params);
            final Integer hostport = (Integer) HOSTPORT.lookUp(params);
            final String dataPath = (String) DATA_PATH.lookUp(params);
            
            if ((searchHost != null && hostport != null || dataPath != null) && indexName != null) {
                return true;
            }
        } catch (IOException e) {
            // ignore
        }
        return false;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public Map<Key, ?> getImplementationHints() {
        return null;
    }

    @Override
    public DataStore createDataStore(Map<String, Serializable> params) throws IOException {
        final String searchHost = (String) getValue(HOSTNAME, params);
        final Integer hostPort = (Integer) getValue(HOSTPORT, params);
        final String indexName = (String) INDEX_NAME.lookUp(params);
        final String searchIndices = (String) SEARCH_INDICES.lookUp(params);
        final String clusterName = (String) getValue(CLUSTERNAME, params);
        final Boolean storeData = (Boolean) getValue(STORE_DATA, params);
        final Boolean localNode = (Boolean) getValue(LOCAL_NODE, params);
        final String dataPath = (String) getValue(DATA_PATH, params);
        return new ElasticDataStore(searchHost, hostPort, indexName, searchIndices, 
                clusterName, localNode, storeData, dataPath);
    }

    @Override
    public DataStore createNewDataStore(Map<String, Serializable> params) throws IOException {
        return null;
    }
    
    private Object getValue(Param param, Map<String, Serializable> params) throws IOException {
        final Object value;
        if (param.lookUp(params) != null) {
            value = param.lookUp(params);
        } else {
            value = param.sample;
        }
        return value;
    }

}
