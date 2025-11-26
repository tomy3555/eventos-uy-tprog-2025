package logica.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.function.Function;
import java.util.function.Consumer;

public final class EntityManagerUtil {
    private static final String PU = "ServidorCentralPU";
    private static volatile EntityManagerFactory emf;

    private EntityManagerUtil() {}

    /** Crea (lazy) y reutiliza el EMF para toda la app */
    public static EntityManagerFactory getEMF() {
        EntityManagerFactory ref = emf;
        if (ref == null) {
            synchronized (EntityManagerUtil.class) {
                ref = emf;
                if (ref == null) {
                    emf = ref = Persistence.createEntityManagerFactory(PU);
                }
            }
        }
        return ref;
    }

    /** Obtiene un EntityManager nuevo (corto alcance) */
    public static EntityManager em() {
        return getEMF().createEntityManager();
    }

    /** Cierra el EMF (llamar al apagar la app/servidor) */
    public static void shutdown() {
        EntityManagerFactory ref = emf;
        if (ref != null && ref.isOpen()) {
            ref.close();
        }
        emf = null;
    }

    /** Plantilla transaccional con lambda: devuelve el resultado de la operación */
    public static <T> T tx(Function<EntityManager, T> work) {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            T out = work.apply(em);
            em.getTransaction().commit();
            return out;
        } catch (RuntimeException ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    /** Versión transaccional para operaciones que no retornan valor */
    public static void txVoid(Consumer<EntityManager> work) {
        tx(em -> { work.accept(em); return null; });
    }

    /** Persistir una entidad dentro de una transacción */
    public static <T> void persist(T entity) {
        txVoid(em -> em.persist(entity));
    }

    /** Mergear (merge) una entidad y devolver la instancia gestionada */
    public static <T> T merge(T entity) {
        return tx(em -> em.merge(entity));
    }

    /** Buscar una entidad por clase y PK (no abre transacción). Cierra el EM al finalizar. */
    public static <T> T find(Class<T> clazz, Object primaryKey) {
        EntityManager em = em();
        try {
            return em.find(clazz, primaryKey);
        } finally {
            em.close();
        }
    }
}