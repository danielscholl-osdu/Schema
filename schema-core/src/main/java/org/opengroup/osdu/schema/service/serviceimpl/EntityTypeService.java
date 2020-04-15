package org.opengroup.osdu.schema.service.serviceimpl;

import org.opengroup.osdu.schema.exceptions.ApplicationException;
import org.opengroup.osdu.schema.exceptions.BadRequestException;
import org.opengroup.osdu.schema.model.EntityType;
import org.opengroup.osdu.schema.provider.interfaces.schemainfostore.IEntityTypeStore;
import org.opengroup.osdu.schema.service.IEntityTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Entity type service to register Entity type.
 *
 */
@Service
public class EntityTypeService implements IEntityTypeService {

    @Autowired
    IEntityTypeStore entityStore;

    /**
     * check and create Entity if its not present of dataPartitionId store
     *
     * @param Entity id
     * @return true or false of successful registration of Entity.
     */
    @Override
    public Boolean checkAndRegisterEntityIfNotPresent(String entityTypeId) {

        try {
            EntityType entityType = new EntityType();
            entityType.setEntityTypeId(entityTypeId);
            entityStore.create(entityType);
        } catch (ApplicationException e) {
            return false;
        } catch (BadRequestException ex) {
            return true;
        }
        return true;

    }

}
