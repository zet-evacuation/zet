/*
 * Mappings.java
 *
 */

package ds.graph;

/**
 *
 * @author Martin Groß
 */
public class Mappings {
    
    public static <T extends Identifiable> String toString(Iterable<T> domain, IdentifiableIntegerMapping<T> mapping) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        boolean isEmpty = true;
        for (T identifiable : domain) {
            isEmpty = false;
            if (mapping.isDefinedFor(identifiable)) {
                if (mapping.get(identifiable) == Integer.MAX_VALUE) {
                    result.append(identifiable.toString() + " = MAX_INT");
                } else {
                    result.append(identifiable.toString() + " = " + mapping.get(identifiable));
                }
            } else {
                result.append(identifiable.toString() + " = UNDEFINED");
            }            
            result.append(", ");
        }
        if (!isEmpty) result.delete(result.length()-2, result.length());
        result.append("]");
        return result.toString();
    }
    
    public static <T extends Identifiable> String toString(Iterable<T> domain, IdentifiableObjectMapping<T,?> mapping) {        
        StringBuilder result = new StringBuilder();
        result.append("[");
        boolean isEmpty = true;
        for (T identifiable : domain) {
            isEmpty = false;
            result.append(identifiable.toString() + " = " + mapping.get(identifiable));
            result.append(", ");
        }
        if (!isEmpty) result.delete(result.length()-2, result.length());
        result.append("]");
        return result.toString();
    }    

}
