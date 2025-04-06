package edu.metrostate.dealership.infrastructure.imports.models

import edu.metrostate.dealership.infrastructure.imports.models.json.DealerJson
import edu.metrostate.dealership.infrastructure.imports.models.xml.DealerXml


data class DatabaseWrapper(
    val database: List<DealerJson>
)

data class DatabaseWrapperXml(
    val database: List<DealerXml>
)