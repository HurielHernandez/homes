package com.revature.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.revature.application.models.Maintenance;



public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer>
{

}
