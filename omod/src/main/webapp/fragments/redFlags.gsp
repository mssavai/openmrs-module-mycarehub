<div class="ke-panel-frame">
    <div class="ke-panel-heading">Red Flags</div>
    <div class="ke-panel-content"  ng-controller="RedFlagsCtrl" ng-init="init()">
        <table class="ke-table-vertical">
            <thead>
            <tr>
                <th>Client Name</th>
                <th>Client Contact</th>
                <th>CCC Number</th>
                <th>Request</th>
                <th>Status</th>
                <th>Progress Date</th>
                <th>Progress By</th>
                <th>Date Resolved</th>
                <th>Resolved By</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="redFlag in redFlags">
                <td>{{redFlag.clientName}}</td>
                <td>{{redFlag.clientContact}}</td>
                <td>{{redFlag.cccNumber}}</td>
                <td>{{redFlag.Request}}</td>
                <td>{{redFlag.Status}}</td>
                <td>{{redFlag.progressDate}}</td>
                <td>{{redFlag.progressBy}}</td>
                <td>{{redFlag.dateResolved}}</td>
                <td>{{redFlag.resolvedBy}}</td>
                <td>
                    <button ng-hide="redFlag.progressDate != '' || redFlag.dateResolved != ''" type="submit" ng-click="setRedFlagInProgress(redFlag.uuid)" class="btn btn-primary">IN PROGRESS</button>&nbsp;
                    <button ng-hide="redFlag.dateResolved != ''" type="submit" ng-click="setRedFlagResolved(redFlag.uuid)" class="btn btn-warning">RESOLVE</button>&nbsp;
                </td>
            </tr>
            </tbody>
        </table>
        <div class="ke-panel-content pagination">Page {{redFlagCurrentPage}} of {{redFlagsPages}} &nbsp;&nbsp;&nbsp;
            <span ng-repeat="pageIndex in createRange(redFlagsPages);">
                <span  ng-show="redFlagCurrentPage == pageIndex">{{pageIndex}}</span>
                <a href="#"  ng-show="redFlagCurrentPage != pageIndex"
                   ng-click="redFlagPagination(pageIndex)">{{pageIndex}}</a> |
            </span>
        </div>
    </div>
</div>