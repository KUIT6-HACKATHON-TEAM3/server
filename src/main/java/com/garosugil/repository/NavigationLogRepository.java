package com.garosugil.repository;

import com.garosugil.domain.navigation.NavigationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NavigationLogRepository extends JpaRepository<NavigationLog, Long> {
}
