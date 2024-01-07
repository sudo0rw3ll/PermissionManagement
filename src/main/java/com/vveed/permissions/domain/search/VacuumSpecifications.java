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

            predicates.add(criteriaBuilder.equal(root.get("added_by"), userId));

            if (!name.equals("")){
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }

            List<VacuumStatus> vacuumStatuses = new ArrayList<>();

            if(!statuses.isEmpty()){
                for(String status : statuses){
                    vacuumStatuses.add(VacuumStatus.valueOf(status));
                }
                predicates.add(root.get("status").in(vacuumStatuses));
            }

            if(dateFrom > 0){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date_created"), new Date(dateFrom)));
            }

            if(dateTo > 0){
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date_created"), new Date(dateTo)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
