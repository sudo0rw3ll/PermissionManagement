package com.vveed.permissions.domain.search;

import antlr.StringUtils;
import com.vveed.permissions.domain.Vacuum;
import com.vveed.permissions.domain.enums.VacuumStatus;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class VacuumSpecifications {

    public static Specification<Vacuum> searchVacuum(Long userId, String name, List<String> statuses, Long dateFrom, Long dateTo){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            List<Predicate> orPredicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("active"), true));
            predicates.add(criteriaBuilder.equal(root.get("added_by"), userId));

            if (!name.equals("")){
                orPredicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }

            List<VacuumStatus> vacuumStatuses = new ArrayList<>();

            if(!statuses.isEmpty()){
                for(String status : statuses){
                    vacuumStatuses.add(VacuumStatus.valueOf(status));
                }
                orPredicates.add(root.get("status").in(vacuumStatuses));
            }

            if(dateFrom > 0){
                orPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateCreated"), new Date(dateFrom)));
            }

            if(dateTo > 0){
                orPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dateCreated"), new Date(dateTo)));
            }

            if (predicates.isEmpty() && orPredicates.isEmpty()) {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Match all records
            }

            if (!orPredicates.isEmpty()) {
                Predicate orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
                predicates.add(orPredicate);
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
