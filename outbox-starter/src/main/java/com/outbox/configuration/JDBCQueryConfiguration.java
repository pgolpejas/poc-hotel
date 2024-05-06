package com.outbox.configuration;

import lombok.Data;

@Data
public class JDBCQueryConfiguration {

    private String updateQuery;

    private String updateAllForRepublishQuery;

    private String findByRestaurationId;

    private String findByAggregateRoot;

    private String findAvailableRootData;

    private String selectCountQuery;

    private String findLatestByAggregateRoot;

    private String findAllPending;

    private String updateForRepublishQueryWithAggregateType;

    private String updateForRepublishQuery;

    private String insertQuery;

    private String findByAggregateBetweenDates;

    private String updateForRepublishQueryWithIds;

    private String findAvailableData;

    private String findNotPublishedQuery;

    private String findByAggregateIdAndAggregateType;

    private String findByAggregateId;

    private String findById;

    private String streamAll;

    private String countById;

    private String findAll;

    private String countAll;

    private String removeOlderThan;

    private String findOlderThan;

    private String streamOlderThan;

    private String countOlderThan;

    private String removeAll;

    private String removeItem;

    private String findPendingByAggregateTypeOlderThan;

    private String findFirstNotPublishedSince;

}

