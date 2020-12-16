package edu.touro.mco152.bm.observers;

import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;

import javax.persistence.EntityManager;

/**
 * This is an observer interface that commits the results from a benchmark to a persisting database.
 */
public class DBObserver implements IObserver{

    /**
     * Persist info about the Write BM Run (e.g. into Derby Database)
     */
    @Override
    public void update(DiskRun run) {
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
