/*
 * Copyright (c) 2021 Red Hat, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Red Hat trademarks are not licensed under GPLv3. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.candlepin.subscriptions.db.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Aggregate for an account.
 *
 * Using an aggregate simplifies modification, as we can leverage JPA to track persistence of hosts, etc.
 */
@Entity
@Table(name = "account_config") // NOTE: we're abusing account_config here, table needs refactor?
public class Account {
    @Id
    @Column(name = "account_number")
    private String accountNumber;

    // NOTE: we'll probably need to do an abstraction per-service type in the future
    @OneToMany(
        cascade = CascadeType.ALL,
        mappedBy = "accountNumber",
        fetch = FetchType.EAGER,
        orphanRemoval = true
    )
    @MapKeyColumn(name = "instance_id")
    private Map<String, Host> serviceInstances = new HashMap<>();

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Map<String, Host> getServiceInstances() {
        return serviceInstances;
    }

    public void setServiceInstances(Map<String, Host> hosts) {
        this.serviceInstances = hosts;
    }
}
