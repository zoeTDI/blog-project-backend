package cn.caldm.www.shared_kernel.security.assembler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface BaseAssembler<D, P> {

    D toDomain(P po);

    default List<D> toDomainList(List<P> poList) {
        if (poList == null || poList.isEmpty()) {
            return Collections.emptyList();
        }
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    P toPO(D domain);

    default List<P> toPOList(List<D> domainList) {
        if (domainList == null || domainList.isEmpty()) {
            return Collections.emptyList();
        }
        return domainList.stream()
                .map(this::toPO)
                .collect(Collectors.toList());
    }
}
